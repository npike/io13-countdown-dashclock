/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.npike.android.googleiocountdown.dashclock;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Intent;
import android.net.Uri;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class ExtensionService extends DashClockExtension {

	private static final String TIMEZONE_SF = "GMT-7";

	@Override
	protected void onUpdateData(int reason) {
		boolean isCountingDownToRegistration = true;
		
		Calendar calendarToday = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_SF)); 

		Calendar calendarIORegistration = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_SF));
		calendarIORegistration.set(2013, Calendar.MARCH, 13, 7, 0, 0);
		
		Calendar calendarIO = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_SF));
		calendarIO.set(2014, Calendar.MAY, 25, 9, 0);
		

		// would use DateUtils.getRelativeTimeSpan string, but I want to break out
		// the day, hour, and minute values.
		long registrationInMilliseconds = calendarIORegistration
				.getTimeInMillis();
		long ioInMilliseconds = calendarIO.getTimeInMillis();

		long nowInMilliseconds = calendarToday.getTimeInMillis();

		long diff = registrationInMilliseconds - nowInMilliseconds;

		if (calendarToday.after(calendarIORegistration)) {
			// start counting down to Google IO. Registration is over!
			diff = ioInMilliseconds - nowInMilliseconds;
			isCountingDownToRegistration = false;
		} 

		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);

		int realHours = (int) (diffHours - (diffDays * 24));
		int realMinutes = (int) (diffMinutes - (diffHours * 60)); 
		
		String shortTitle = "";
		String relativeTime = "";

		String days = getResources().getQuantityString(R.plurals.days,
				(int) diffDays, (int) diffDays);

		String hours = getResources().getQuantityString(R.plurals.hours,
				realHours, realHours);

		String minutes = getResources().getQuantityString(R.plurals.minutes,
				realMinutes, realMinutes);

		// Figure out what the short title should be.
		if (diffDays > 0) {
			shortTitle = String.format(Locale.US,
					getString(R.string.short_title_days), diffDays, realHours);
		} else if (diffHours > 0) {
			shortTitle = String.format(Locale.US,
					getString(R.string.short_title_hours), realHours, realMinutes);
		} else if (diffMinutes > 0) {
			shortTitle = String.format(Locale.US,
					getString(R.string.short_title_minutes), realMinutes);
		}

		// Figure out what the expanded body will be.
		if (diffDays > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_days), days, hours,
					minutes);
		} else if (diffHours > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_hours), hours, minutes);

		} else if (diffMinutes > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_minutes), diffMinutes);
		}

		String expandedBody = "";
		String expandedTitle = "";

		if (isCountingDownToRegistration) {
			expandedBody = String.format(Locale.US,
					getString(R.string.expanded_body_registration), relativeTime);

			if (relativeTime.length() == 0) {
				shortTitle = getString(R.string.short_title_now);
				expandedBody = getString(R.string.expanded_registration_over_good_luck_may_the_odds_be_ever_in_your_favor);
			}

			expandedTitle = getString(R.string.expanded_title_registration);
		} else {
			expandedBody = String.format(Locale.US,
					getString(R.string.expanded_body_io), relativeTime);

			if (relativeTime.length() == 0) {
				shortTitle = getString(R.string.short_title_now);
				expandedBody = getString(R.string.expanded_io_here);
			}

			expandedTitle = getString(R.string.expanded_title_io);
		}

		publishUpdate(new ExtensionData()
				.visible(true)
				.icon(R.drawable.ic_action_icon_dark)
				.status(shortTitle)
				.expandedTitle(expandedTitle)
				.expandedBody(expandedBody)
				.clickIntent(
						new Intent(Intent.ACTION_VIEW, Uri
								.parse(getString(R.string.url_io)))));
	}
}
