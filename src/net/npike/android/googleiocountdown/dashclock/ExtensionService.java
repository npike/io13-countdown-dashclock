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

import android.content.Intent;
import android.net.Uri;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class ExtensionService extends DashClockExtension {

	@Override
	protected void onUpdateData(int reason) {
		Calendar calendarIO = Calendar.getInstance();
		calendarIO.set(2013, 2, 14, 7, 0);

		long milliseconds1 = calendarIO.getTimeInMillis();
		long milliseconds2 = System.currentTimeMillis();
		long diff = milliseconds1 - milliseconds2;
		long diffSeconds = diff / 1000;
		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);

		String shortTitle = "";
		String relativeTime = "";

		String days = getResources().getQuantityString(R.plurals.days,
				(int) diffDays, (int) diffDays);
		String hours = getResources().getQuantityString(R.plurals.hours,
				(int) (diffHours - (diffDays * 24)),
				(int) (diffHours - (diffDays * 24)));
		String minutes = getResources().getQuantityString(R.plurals.minutes,
				(int) (diffMinutes - (diffHours * 60)),
				(int) (diffMinutes - (diffHours * 60)));

		if (diffDays > 0) {
			shortTitle = days;
		} else if (diffHours > 0) {
			shortTitle = hours;
		} else if (diffMinutes > 0) {
			shortTitle = minutes;
		}

		if (diffDays > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_days), days, hours,
					minutes);
		} else if (diffHours > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_hours), diffHours
							- (diffDays * 24), diffMinutes - (diffHours * 60),
					diffSeconds - (diffMinutes * 60));

		} else if (diffMinutes > 0) {
			relativeTime = String.format(Locale.US,
					getString(R.string.full_countdown_minutes), diffMinutes
							- (diffHours * 60), diffSeconds
							- (diffMinutes * 60));
		}

		String expandedBody = String.format(Locale.US,
				getString(R.string.expanded_body), relativeTime);

		if (relativeTime.length() == 0) {
			shortTitle = getString(R.string.registration_over);
			expandedBody = getString(R.string.expanded_registration_over_good_luck_may_the_odds_be_ever_in_your_favor);
		}

		publishUpdate(new ExtensionData()
				.visible(true)
				.icon(R.drawable.ic_action_icon_dark)
				.status(shortTitle)
				.expandedTitle(getString(R.string.expanded_title))
				.expandedBody(expandedBody)
				.clickIntent(
						new Intent(Intent.ACTION_VIEW, Uri
								.parse(getString(R.string.url_io)))));
	}
}
