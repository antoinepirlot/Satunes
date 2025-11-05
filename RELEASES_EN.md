# Releases (🇬🇧)

Tu peux retrouver ce fichier en [français 🇫🇷](RELEASES_FR.md)

## 3.3 (Android 5.1.1 Lollipop and later)

### 3.3.1

Improvements, optimisations and bug fixes:

* Fix folder by removing "External Storage: " for all sub folders

### 3.3.0

New features:

* When sorting media into another media type, it shows the other media's title instead of first
  letter. (For example, sorting music by albums for each album you'll see the album title.)
* Add a ".." folder to go back
* New setting in library to choose if the music's title is the file name of tag in metadata.
* Option to reverse sorting
* Path in folder view is clickable to navigate

Improvements, optimisations and bug fixes:

* Fix updates check other channel than stable.
* Fix sorting crash by dates
* Added a space between the form and the button in the timer form dialog
* Update design of media list
* Fix sorting options when leaving sort dialog without applying modification, previous values are
  restored
* Fix forward and reward modification takes directly effect
* Fix going back to the default view when rotating or changing light/dark mode
* Some optimisation
* Update libraries

### 3.3.0-preview-2

Improvements, optimisations and bug fixes:

* Fix buttons from media list showing where it should not
* Show "External Storage: [storage name]" in folder view for storage like sd cards
* Fix going back to the default view when rotating or changing light/dark mode

### 3.3.0-preview-1

Improvements, optimisations and bug fixes:

* Click on path button will now go ahead instead of going back
* Fix forward and reward modification takes directly effect
* Fix crash when rotating screen
* Fix wrong bottom bar highlight in certain views
* Maybe some other unknown bugs fixed

### 3.3.0-beta-2

New features:

* Path in folder view is clickable to navigate

Improvements, optimisations and bug fixes:

* Fix updates check other channel than stable.
* Remove the letter separator in the playback list view and album view
* Fix sorting crash by dates
* Added a space between the form and the button in the timer form dialog
* Fix sorting options when leaving sort dialog without applying modification, previous values are
  restored
* Music in different storage are no more on the first folder alongside the "This device" folder
* Improve design of list
* No more folder associated to the file name created
* The back folder can be clicked once by view (it prevents going to blank view if clicked too fast)

### 3.3.0-beta-1

New features:

* When sorting media into another media type, it shows the other media's title instead of first
  letter. (For example, sorting music by albums for each album you'll see the album title.)
* Update design of media list
* Add a ".." folder to go back
* New setting in library to choose if the music's title is the file name of tag in metadata.
* Option to reverse sorting

Improvements, optimisations and bug fixes:

* Some optimisation
* Update libraries

## 3.2 (Android 5.1.1 Lollipop and later)

### 3.2.3

Improvements, optimisations and bug fixes:

* Fix the action on artist banner in album's view.
* Fix the export of a single playlist from playlist's option does no more export all playlist.

### 3.2.2

Improvements, optimisations and bug fixes:

* Bring back Satunes to Android 5.1.1 (it was a mistake, my apologies)

### 3.2.1

Improvements, optimisations and bug fixes:

* Fix issue while opening music from file explorer app on Samsung devices

### 3.2.0

New features:

* Ability to open an audio file from the file explorer
* Support for the M3U format for playlists (see import/export).

Improvements, optimisations and bug fixes:

* Fixed update verification
* Remove artist button from Album's dialog if the current view is the artist.

### 3.2.0-preview-3

Improvements, optimisations and bug fixes:

* Fixed bug preventing folders from opening
* Fixed issue when opening music that was not part of the loaded music
* Fixed update verification

### 3.2.0-preview-2

Improvements, optimizations and troubleshooting:

* Remove artist button from Album's dialog if the current view is the artist.
* Fix crash when opening music from file explorer app when a playback is already running and the
  music not in the playback.
* Fix allowing opening the same music in the session.

### 3.2.0-preview-1

New features:

* Ability to open an audio file from the file explorer

### 3.2.0-beta-1

New features:

* Support for the M3U format for playlists (see import/export).

## 3.1 (Android 5.1.1 Lollipop and later)

### 3.1.3

Improvements, optimizations and troubleshooting:

* Fix crash when using sorts in a folder view

### 3.1.2

Improvements, optimizations and troubleshooting:

* Fix play buttons not showing in folders views
* Fix back gesture with swipe navigation (predictive back gesture is temporarily disable)

### 3.1.1

Improvements, optimizations and troubleshooting:

* Fix matching settings ui after reset some settings
* Clarify the name of the added to playlist sorting option
* Fix order of music played from folder on not shuffle mode
* Fix disc widget refresh in certain conditions

### 3.1.0

New features:

* Settings to round artworks and animate when playing
* New Widget "Disc"
* The include/exclude feature now takes in account both cases. You now can include some folders and
  exclude other ones at the same time.
* Switch to turn off logs
* Update channel subscriptions

Improvements, optimizations and troubleshooting:

* Fix the too long path length that hides trash icon in library settings
* Fix playing music crash after audio permission allowed
* Hide buttons to play music on media's view to avoid playing music while data loading
* On add to next or add to queue, musics are added randomly if the default shuffle mode is enabled

### 3.1.0-preview-1

New features:

* Update channels subscriptions

Improvements, optimizations and troubleshooting:

* Fix modal on opening library settings, it will now be permanently hidden on "ok" button click
* Include/exclude switch is now a simple button again. (No more switch action)
* Fix the too long path length that hides trash icon in library settings
* Make artwork circle in Android Auto
* Fix clickable zone of circle artwork in playback view (it's now a circle)
* Tentative of wrong widget loading after a moment

### 3.1.0-beta-1

New features:

* Settings to round artworks and animate when playing
* New Widget "Disc"
* The include/exclude feature now takes in account both cases. You now can include some folders and
  exclude other ones at the same time.
* Switch to turn off logs

Improvements, optimizations and troubleshooting:

* Fix playing music crash after audio permission allowed
* Hide buttons to play music on media's view to avoid playing music while data loading
* On add to next or add to queue, musics are added randomly if the default shuffle mode is enabled

## 3.0 (Android 5.1.1 Lollipop and later)

### 3.0.5

Improvements, optimizations and troubleshooting:

* Change tipeee button by Liberapay

### 3.0.4

Improvements, optimizations and troubleshooting:

* Fix list position on sort change
* Center toast and buttons in settings
* Remove separating lines added in media selection modal added by error

### 3.0.3

Improvements, optimizations and troubleshooting:

* Fix issue crashing the app using sd card

### 3.0.2

Improvements, optimizations and troubleshooting:

* Improve list behavior
* Fix modal popup (rollback to the old system that was more efficient)
* Fix exclude folders feature
* Remove useless permission "manage_external_storage"
* Fix navigation:
    * If audio is not allowed, settings are accessible
    * If audio has just been allowed in-app, the navigation is then full working

### 3.0.1

Improvements, optimizations and troubleshooting:

* Fix crash when an sd card is loaded. Sorting by last added date temporarily unavailable for files
  stored in sd cards.

### 3.0.0

New features:

* Ability to sort media by artist, album and/or genre based on page
* Display of the first letter in alphabetical order above the first media (with this letter) in the
  list (can be disabled in library settings)
* Added sorting by year and last added for music (in some views at the moment)
* Adding media to playlists now removes them if they are present (entirely for those who are not
  music)
* It's possible to move custom actions positions
* A playlist can be selected as the default one

Improvements, optimizations and troubleshooting:

* Support for Android 15 (Vanilla Ice Cream)
* Update texts
* When the user changes the view from one media type to another, the navigation bar reflects the
  modifications
* Music loading issues causing crashes under some conditions fixed (Concurrent Modification)
* Satunes's libraries update that involve a slight design change of some elements (like the progress
  bar)
* Other indirect optimizations and corrections
* Changed update source from GitHub to Codeberg
* GitHub button replaced by Codeberg

### 3.0.0-preview-1

Improvements, optimisations and troubleshooting:

* Fixed playlist selection being displayed when going to playback view
* Faster startup
* Fixed clicking on music in playback list
* Media list doesn't return to first item if media options close
* Fixed navigation issues
* Centered artist icon next to artist name
* Changed update source from GitHub to Codeberg
* GitHub button replaced by Codeberg

### 3.0.0-beta-2

Improvements, optimizations and troubleshooting:

* Fix some UI issues
* Update texts
* Remove some cancel actions when updating playlist as it is more complex now
* Fix crashing when loading not existing music (yes it could happen with MediaStore lol)
* Fix opening default playlist
* Reset the older order of extra buttons list on playlist view
* Fix search view
* Other indirect optimizations and improvements

### 3.0.0-beta-1

New features:

* It's possible to move custom actions positions
* A playlist can be selected as the default one

Improvements, optimizations and troubleshooting:

* Update texts
* Add artist icon next to artist title in certain views
* Update position of elements in update view
* Move settings dedicated to design in Design settings menu
* Update code structure
* Fix some ui issues from 3.0.0-alpha-2
* Other indirect optimizations and improvements

### 3.0.0-alpha-2

New features:

* Added sorting by year and last added for music (in some views at the moment)
* Adding media to playlists now removes them if they are present (entirely for those who are not
  music)

Improvements, optimizations and troubleshooting:

* Startup optimization
* When the list is sorted, the list loaded in playback corresponds to the sort
* When the user changes the view from one media type to another, the navigation bar reflects the
  modifications
* Other indirect optimizations and improvements

### 3.0.0-alpha-1

New features:

* Ability to sort media by artist, album and/or genre based on page
* Display of the first letter in alphabetical order above the first media (with this letter) in the
  list (can be disabled in library settings)

Improvements, optimizations and troubleshooting:

* Support for Android 15 (Vanilla Ice Cream)
* Music loading issues causing crashes under some conditions fixed (Concurrent Modification)
* Satunes's libraries update that involve a slight design change of some elements (like the progress
  bar)
* Other indirect optimizations and corrections

## 2.5 (Android 5.1.1 Lollipop and later)

### 2.5.2

Improvements, optimizations and troubleshooting:

* Hide start timer if the timer is running

### 2.5.1

Improvements, optimizations and troubleshooting:

* Add "Developed in Europe 🇪🇺" at the end of About settings section
* Fix snack bar not showing '%' on folder's path removed
* Fix Music control bar time when leaving and going back to the screen after a moment when playing
* Fix if forward rewind new value is <= 0 then old value is kept
* Show info dialog if setting need reload library

### 2.5.0

New features:

* Add playlist's musics to another playlist
* Jump back/ahead in music playback
* Reset Settings

Improvements, optimizations and troubleshooting:

* Timer setting will show remaining time inside the number fields
* Few minor optimization

## 2.4 (Android 5.1.1 Lollipop and later)

### 2.4.6

Improvements, optimizations and troubleshooting:

* Fix Android Auto not playing the selected music and not respecting the default shuffle mode

### 2.4.5

Nothing new, issue happened while updating

### 2.4.4

Improvements, optimizations and troubleshooting:

* Remove exclusion of FOSS libraries
* Disable the inclusion dependency metadata in APK and Bundle as it's not FOSS compatible

### 2.4.3

Improvements, optimizations and troubleshooting:

* The queue list button is now a switch like the setting button
* Fix issue when nav bar section is disabled, it doesn't prevent user to go on its views
* The widget is now resizable vertically but is limited (it fix wrong layout in certain launcher by
  allowing user to resize it)

### 2.4.2

Improvements, optimizations and troubleshooting:

* Add musics to playlists works again

### 2.4.1

Improvements, optimizations and troubleshooting:

* When canceling media selection, the selected media are no longer saved
* When selecting the default menu bar, the application no longer redirects to the new menu

### 2.4.0

New features:

* Ability to like music in Android Auto
* Timer that can pause music (maximum for 8 hours)

Improvements, optimizations and troubleshooting:

* Playing the current music from the same loaded playlist does not reload the playback
* Music is sorted by track number in albums and then, otherwise, alphabetically
* The name of the exported playlist file and logs contains a date and time
* Improved widget refresh
* The year of the album is displayed, if it exists
* Update buttons are put in a column to improve their visibility on some screens
* Like button in Android Auto has been swapped with shuffle button
* Fixed track switching to previous under certain conditions

Removed features:

* Removed the playlist visualization in Android Auto to avoid distractions

### 2.4.0-preview-1

Improvements, optimizations and troubleshooting:

* Fix v2.4.0-beta-1 issues
    * Playing music crash in certain conditions
    * Add music into playlists
    * Show missing remove music from playlist
    * Android Q and less crash at launching

### 2.4.0-beta-1

New features:

* Ability to like music in Android Auto
* Timer that can pause music (maximum for 8 hours)

Improvements, optimizations and troubleshooting:

* Playing the current music from the same loaded playlist does not reload the playback
* Music is sorted by track number in albums and then, otherwise, alphabetically
* The name of the exported playlist file and logs contains a date and time
* Improved widget refresh
* The year of the album is displayed, if it exists
* Update buttons are put in a column to improve their visibility on some screens

Removed features:

* Removed the playlist visualization in Android Auto to avoid distractions

## 2.3 (Android 5.1.1 Lollipop and later)

### 2.3.3

Improvements, optimizations and troubleshooting:

* Fix Android 15 unintended ui issues by rolling back targetSdk to Android 14

### 2.3.2

Improvements, optimizations and troubleshooting:

* Widget refreshing has been improved
* Bring back horizontal padding on playback view for texts

### 2.3.1

Improvements, optimizations and troubleshooting:

* Remove Android Auto limit
* By default Satunes load music's artist if there's no album's artist
* Loading library won't show warning modal
* "Clean playlists" will show warning modal
* Change some text
* Playlist's title won't accept backlines or tabulations
* Buttons rows scrolling zone is no more cut
* Log file size is max 5MB and when Satunes update the current position in playback view, no more
  logs to optimize battery
* Fix wrong repeat mode after reloading library
* Make search setting view scrollable
* Fix playing next music in certain conditions

### 2.3.0

New features:

* Be able to shuffle and change repeat mode with notification
* Share medias files
* Long click on album artwork opens the album's options
* Long click on artist text in playback view opens the artist's options
* Be able to load music's artist if the album's artist is unknown (library settings)
* Clean playlists to removed saved music if they don't exists in loaded musics instead of cleaning
  automatically

Improvements, optimizations and troubleshooting:

* Fix importing single playlist
* Fix importing playlist when it already exists
* Some optimizations
* Add Fdroid button in about setting's section
* Playlist settings have been moved to Library settings
* Fix play next in certain conditions
* Fix remove from queue in certain conditions
* Fix crash while launching on Android 9 and earlier if read external storage permission is not
  allowed

## 2.2 (Android 5.1.1 Lollipop and later)

### 2.2.4

Improvements, optimizations and troubleshooting:

* If files with size of 0 or duration of 0, then it won't be loaded
* If the playback view wants to show a NaN Float value for the current position, it will show 00:00
  instead of crashing app

### 2.2.3

Improvements, optimizations and troubleshooting:

* Completely disable bottom navbar items when switched off
* The default section cannot be a switched off navbar section
* Fix spelling in a french string
* Add space between content text and buttons in playlists settings view

### 2.2.2

New features:

* Ability to undo the deletion of a path

Improvements, optimizations and troubleshooting:

* Add feedback haptic on extra buttons
* Fixed the display of paths, now centered
* Changed some texts
* Opening the application will always take the default section setting into account
* Next music can be removed from the playlist
* Genres display albums again
* When a music before the current music is put next, the list stays in sync with the order

### 2.2.1

Improvements, optimizations and troubleshooting:

* Fix crash on Android 10 (Quince Tart) and older versions
* Update some strings

### 2.2.0

New features:

* Support of compilation
* Classic Widget
* Selection of default navbar section

Improvements, optimizations and troubleshooting:

* The "Folders" setting becomes "Library"
* Optimize Android Auto startup
* Hide tab section highlight when user is in settings, search or playback views
* Fix crash when Satunes has been not used for a moment and reopen from multi-task
* Play next is hidden on next music's options
* Some other optimizations

## 2.1 (Android 5.1.1 Lollipop and later)

### 2.1.2

Improvements, optimizations and troubleshooting:

* Add new line at the end of log line
* Fix no path selected and include setting selected

### 2.1.1

Improvements, optimizations and troubleshooting:

* Fix Android Auto issues
* Fix app crash everytime the app launch itself with Android Auto
* Path:
    * Show "this device" instead of "/0"
    * Move refresh button to a new line
* Optimize search process

### 2.1.0

New features:

* It is now possible to include/exclude paths
* Export single playlist is back
* Remove media from queue
* Add to next and to queue for all media
* After music(s) has/have been added to playlist(s) the snackBar has cancel action. The same after
  canceling, you can cancel the cancel action lol

Improvements, optimizations and troubleshooting:

* RAM usage has been significantly reduced, artworks are loaded when needed
* Playback is more stable when it is released

## 2.0 (Android 5.1.1 Lollipop and later)

### 2.0.1

Improvements, optimizations and troubleshooting:

* It is no longer possible to have 2 different playlists with the same name regardless of the
  upper/lower case
* Blank strings are removed from playlist title when added or updated.
* When launching playback via searches with shuffle mode, the selected music is the first in the
  queue
* When searching, if the first or last characters are spaces, they are ignored

### 2.0.0

Improvements, optimizations and troubleshooting:

* Restructured the code
* Improved performance and speed of loading and accessing data
* Android Auto is more stable although Android Auto has some constraints in terms of data quantity
* Importing and exporting playlists works correctly
* The progress bar will display the predefined levels in this order:
    * Very slow
    * Slow
    * A little slow
    * Normal
    * Fast
    * Very fast
    * In real time
* Defined an order for media if their title is exactly the same when comparing,
  on the screen if this happens, you will see the media in this order:
    * Music
    * Album
    * Artist
    * Genre
    * Playlist
    * Folder
* Use of snack bars for different notifications
* Added the album title on the music item to differentiate albums and songs
* The buttons for the shuffle settings are no longer switches but act as the buttons
  for the repeat mode
* Now, the application will even load your duplicated music
* By default, Satunes will only load the main Music folder and not the whole thing.
* Satunes records some errors via logs (without personal information), you can export them, nothing
  will leave the application without your permission.
* Now, playing music from a folder will first load its music, then those
  from its subfolders, always sorted by title.
* Added icons to identify settings more easily.

### 2.0.0-preview-1

Improvements, Optimizations and Troubleshooting:

* Use snack bar for notifications
* Playlists import/export has been fixed and now works as expected
* Refactor Android Auto, make it more stable and tried to fix issues with it
* Add album title on music item to differentiate albums and musics
* Fix closing app when playing while setting to keep it in playing mode disabled
* Progress bar will show predefined levels in this order:
    * Very Slow
    * Slow
    * A bit slow
    * Normal
    * Fast
    * Very Fast
    * Real Time
* Do not close modal when unliking music from likes playlist view
* Some other optimizations

### 2.0.0-beta-1

Improvements, Optimizations and Troubleshooting:

* Defined an order for media if their title is exactly the same while comparing, on the screen if it
  happens you will see media in this order:
    * Music
    * Album
    * Artist
    * Genre
    * Playlist
    * Folder
* Limit the item list with 300 element max in Android Auto due to its limitations
* Loading should be faster in Android Auto as one loop has been removed for each list loading
* The shuffle setting buttons are no more switches but act like repeat buttons
* Fix musics from external storage like sd card not loaded

### 2.0.0-alpha-1

⚠ Alpha version is not recommended for regular use.

Improvements, Optimizations and Troubleshooting:

* The code structure has been fully refactored to be more simple and better to use.
* The code has been reworked to be faster
* Satunes now support Android 15 Vanilla Ice Cream (API 35)
* Now the app will load even your duplicated musics
* By default, Satunes will load only the main Musics folder and not all.
* Satunes logs some errors (without personal information), you can export it, nothing will leave the
  app without your permission.
* Implementing the use of ViewModel and UiState for a better Satunes' states management
* Now playing music from folder will first load its musics then its subfolders' musics always sorted
  by title.
* Add icons in settings to identify them easily.

## 1.3 (Android 5.1.1 Lollipop and later)

### 1.3.1

Improvements, Optimizations and Troubleshooting:

* Musics in folder are no more modified when the user click on music to play it
* Search icon has been remove from the search setting view

### 1.3.0

New features:

* Possibility of choosing the search filters activated by default
* Request for confirmation when deleting from playlists
* All types of media (music, artists, etc.) have an option menu during a long press

Improvements, Optimizations and Troubleshooting:

* Added a button to open F-Droid when an update is available

## 1.2 (Android 5.1.1 Lollipop and later)

### 1.2.1

Improvements, Optimizations and Troubleshooting:

* Fixed bug when adding currently playing music to a playlist when there is none and the user
  cancels its creation
* Added search filter for playlists
* Navigation animations between pages are faster and avoid clicking on the previous page
* The music queue list page opens with the currently playing music in mind
* Added a button to open currently playing music from the search page
* The music filter in the search is activated by default

### 1.2.0

New features:

* Search for different media
* Favorite music system
* Viewing the playlist and being able to:
* Play music after current track
* Add to queue
* Added a tablet mode for displaying music currently playing
* Popup after installing an update

Improvements, Optimizations and Troubleshooting:

* Removal of the Facebook button following the future disappearance of the Satunes' Facebook page
* Optimizations
* A bug causing the application to crash following the creation of the first playlist has been fixed

### 1.2.0-preview-1

New features:

* Search for different media
* Favorite music system
* Viewing the playlist and being able to:
* Play music after current track
* Add to queue
* Added a tablet mode for displaying music currently playing
* Popup after installing an update

Improvements, Optimizations and Troubleshooting:

* Removal of the Facebook button following the future disappearance of the Satunes' Facebook page
* Optimizations

## 1.1 (Android 5.1.1 Lollipop and later)

### 1.1.1

Improvements, Optimizations and Troubleshooting:

* When playing the next music when it is already the last one after shuffling.
* Playlists load faster at starting

### 1.1.0

Improvements, Optimizations and Troubleshooting:

* The size of the repeat one mode icon in the default playback settings displays correctly on
  different screen sizes
* Updating libraries

### 1.1.0-beta-1

New features:

* Added a new setting allowing the use of Audio Offload (sends playback to a dedicated process).
  This option allows you to optimize the battery
* Ability to add currently playing music to playlists via quick actions
* Navigation to albums, genres, artists, etc. from the menu with a long press on music

Improvements, Optimizations and Troubleshooting:

* Display issue when launching first music in Android Auto fixed
* The default random mode selection is represented by buttons with icons and more using a switch
* Displaying navigation bar button names

## 1.0 (Android 5.1.1 Lollipop and later)

### 1.0.3

Improvements, Optimizations and Troubleshooting:

* The album is no longer displayed in playback if the screen height size is too small in landscape
  mode
* Fixed crash when clicking the parameter button when audio permission is not granted
* Default language if system language is not supported is now English and no longer French
* Album titles will no longer exceed the width of the album
* Fixes to the text size of the "Music" section in the artist and genre views
* Adding music to several playlists is now resolved
* Fixed issue when adding 2 playlists with the same name with upper and lower case on different
  letters
* When adding music to a playlist and no playlist has been created, the application starts creating
  a new playlist

### 1.0.2

Improvements, Optimizations and Troubleshooting:

* Settings navigation works consistently for all menus.
* Design improvement

### 1.0.1

Improvements, Optimizations and Troubleshooting:

* Fix icons on playback views no more showing full black or full white when deactivated
* Harmonize shuffle and repeat icon by making the ones in settings with round border
* Adding a horizontal padding to Android Auto setting view
* Fix downloading release update

### 1.0.0

Satunes is now stable!

Improvements, Optimizations and Troubleshooting:

* Navigating to genres or albums with a name containing the '/' character can now work normally
* The design has been improved:
    * The shuffle and repeat buttons in the playback view (only in the phone app) are completely
      round
    * Support for screen sizes has been improved
* Added a menu in settings explaining how to activate Android Auto

### 1.0.0-preview-2

New features:

* Ability to change the name of a playlist

Improvements, Optimizations and Troubleshooting:

* Problems related to playback in Android Auto have been fixed:
    * No display at launch
    * Progress bar during repeat one mode that goes beyond the time limit
* Added text explaining why exporting and exporting playlists is in beta
* Attempted to fix crash when opening the app after a long time after pausing

### 1.0.0-preview-1

This version is planned to be deployed as a stable version if no bugs are detected during this
phase.

New features:

* Added a shuffle button to all Android Auto views
* Removed the "Musics" tab because there were too many elements in Android Auto.
* New settings:
    * Choice of repeat mode
    * Choice of shuffle mode
    * Ability to prevent other applications from pausing/playing playback

Improvements, Optimizations and Troubleshooting:

* Accents are sorted correctly
* Progress bar no longer glitches when enabling/disabling shuffle mode
* Background playback works correctly depending on the chosen setting
* Updates will take into account alpha, beta and preview 1,2,3, etc. for example "1.0.0-preview-1"
* Multi-language is now supported in Android Auto
* The shuffle button in Android Auto has an icon
* Edited link for github
* Improved design and support for different screen sizes.

## 0.10.0 (Android 5.1.1 Lollipop and later)

### 0.10.1-beta

Improvements, Optimizations and Troubleshooting:

* Songs are sorted alphabetically in folders, this avoids wrong playing order.
* Optimization of performance and battery when playing. The progress bar is refreshed only when the
  relevant view is open.

### 0.10.0-beta

The app is now available for Android Lollipop (5.1.1) and newer devices.
About 99.2% of you can install it 😜.

New features:

* It is possible to change the refresh rate of the progress bar
* It is possible to export and import playlists, in json format (future improvements will be
  developed.)
* It is possible to exclude ringtones

Improvements, Optimizations and Troubleshooting:

* The way permissions are managed has been improved and the application no longer crashes on first
  start
* The design has been improved:
    * In order to avoid blocking on small screens, all elements are scrollable while waiting to find
      a
      more suitable solution
    * Album image is displayed on screen in album page
    * The different views are scrollable in their entirety
    * The genre view displays, in addition to music, albums as in an artist's page
    * The artist name is displayed in the album view and allows redirection to it
    * The text areas have been harmonized and long texts will be cut if necessary
* Some light optimizations have been made

## 0.9 (Android 9 Pie and later)

### 0.9.2-beta

Improvements, Optimizations and Troubleshooting:

* Improved sorting
* Albums contain more than one song
* Taking into account the case where no data has been loaded.

### 0.9.1-beta

Improvements, Optimizations and Troubleshooting:

* Changed domain name to "io.github.antoinepirlot.satunes".
* The design has been reworked to bring default album art throughout the application.
* Album art displays in Android Auto.
* Database migrations can be done.
* Fixed some issues when initializing Android Auto in some cases.
* Screen recomposition when adding data such as playlists is now operational.

### 0.9.0-beta

MP3 Player finally has its own name "Satunes". This version becomes compatible with Android 9 Pie
and newer.

New features:

* Clicking on the notification opens the application where the user left it.
* Compatibility with Android 9 Pie and newer
* Ability to add several songs to a playlist
* Ability to start shuffle and repeat a song in Android Auto

Improvements, Optimizations and Troubleshooting:

* The design has been improved.
* Parameters have been moved to subsections for better separation.
* Updates can be done regardless of the name of the application
* Toasts will no longer be displayed twice when updating.
* The album image is displayed in the album and music lists to make the lists more user-friendly.
* Optimization of battery usage
* Launching music from playlists is now fixed.
* The library which allows navigation has been updated and brings new animations.

## 0.8 (Android 11 Red Velvet Cake and later)

### 0.8.3-beta

Improvements, Optimizations and Troubleshooting:

* All albums that have the same name will be displayed on the screen.
* Artists, albums and genres without names will have an associated name.

### 0.8.2-beta

Improvements, Optimizations and Troubleshooting:

* Albums contain all their musics. An album is considered identical if its title and artist are the
  same.
* Now it is possible to download and install the update from the application.

### 0.8.1-beta

Improvements, Optimizations and Troubleshooting:

* When the user has added music to playlists but decides to delete them from the device, the
  application deletes missing music from playlists. This prevents the application from crashing
  every time you launch it.
* The update system has been redesigned. Now the user needs to click a button in the settings. This
  is no longer automatic in order to avoid unnecessary data consumption.
* Update checking has been fixed and now takes into account 4 types of updates:
    * Alpha: These are the most experimental and buggy versions. Features are not set in stone and
      may
      disappear with a subsequent update.
    * Beta: These are test versions to resolve problems with new features. This will be permanently
      added to the application (except in cases of major forces).
    * Preview: This is a release candidate which, if all goes well, will be deployed as a stable
      version.
    * Stable: This is a ready-to-use version. This is the recommended version for normal use.
      However, it may still present some bugs but these will be fixed
      rare and will not prevent the functionality from functioning properly.
* Adding music to several playlists is basically fixed.
* Crashes occurring when recomposing the UI following changes such as "Screen rotation" or "
  Activation of dark mode" will no longer cause the application to crash.
* The navigation has been corrected, now the user can return to other main screens from the settings
  following a recomposition of the screen as indicated in the previous point.
* When launching the application, it should no longer crash due to problems related to playback
  initialization.
* The data structure has been revised to resolve certain problems such as opening an album that was
  not the correct one or causing the application to crash when 2 albums have the same name.
* Optimization of data loading as well as the change of random mode (thanks to Kotlin coroutines).
* The progress bar has been collapsed on the sides to avoid conflict with gesture navigation.
* Playback in Android Auto has been fixed. This will result in a possible increase in battery usage
  but this has not yet been verified.

### 0.8.0-beta

New features:

* It is possible to check if an update is available from settings.
* Added new sections to the settings page.
* Added title to show where the user is located.
* Added a horizontal list displaying all artist's albums.
* Possibility to click on album (in the artist page or from the currently playing music) to display
  the album page.

Improvements, Optimizations and Troubleshooting:

* The setting that allows you to hide the file tab is repaired, the setting is saved correctly.
* The display of the settings page has been redesigned and improved.

## 0.7 (Android 11 Red Velvet Cake and later)

### 0.7.3-beta

Improvements, Optimization and Troubleshooting:

* Data loading has been optimized and is much faster.
* All text displayed in Android Auto is now escaped to see the correct characters.

### 0.7.2-beta

Improvements, Optimization and Troubleshooting:

* Navigation between pages is more intuitive
* Corrections made to playback
* Special characters are escaped, this fixes problems opening folder/media with a name containing a
  special character (for example: Hip-Hop/Rap)

### 0.7.1-beta

Now the app has its own icon 🤗 (note that I'm not an artist 🤭).

Problems resolutions:

* The button to create a playlist is now located at the bottom right of the screen.
* The playback has been overhauled and changing tracks should not cause any problems with the
  display of the music currently playing.
* A loading circle appears at launch when there is a lot of music to load to avoid a slower app
  launch.
* Music no longer automatically pauses in Android Auto when changing music from the queue list.

### 0.7.0-beta

Features:

* Add Playlist System

## 0.6 (Android 11 Red Velvet Cake and later)

### 0.6.0-beta

This version make the app compatible with Android 11 Red Velvet Cake and later.

## 0.5 (Android 14 Upside Down Cake and later)

### 0.5.1-beta

Fix issues:

* Optimisation of Android Auto
* Playback is correctly synchronized

### 0.5.0-beta

Features:

* Android Auto support
    * To activate Android Auto you can find instructions [here](README_EN)

Fix issues:

* Optimisation
* Show all music information on notification

## 0.4 (Android 14 Upside Down Cake and later)

### 0.4.2-beta

Fix issues:

* The media is paused when another media is playing

### 0.4.1-beta

Fix issues:

* Musics, Albums, etc. (Media) are sort by their title.

### 0.4.0-beta

Features:

* Show genres list and play music from them
* Settings
    * Allow users to stop the playback if the app is closed from multitasking
    * Allow users to pause playback if the headset is disconnected (cable or bluetooth)

Fix issues:

* Music from external storage can be played
* Show main storage as Root folder (e.g. "This Device" or "External Storage: C45-54")

## 0.3 (Android 14 Upside Down Cake and later)

### 0.3.2-beta

Fix issues:

* Disabling shuffle activation from outside the app (for example asking assistant to shuffle)

### 0.3.1-beta

Fix issues:

* Loading data on galaxy devices won't crash the app if not artwork found
* Disconnecting headset or bluetooth device will pause music
* The button play paused is now correctly synchronised with the state of the music

### 0.3.0-beta

New functionalities has landed. Now you can

* See album list and play music from albums
* Setting Page
    * Change bottom bar tabs by removing folders, artists or albums tabs

## 0.2 (Android 14 Upside Down Cake and later)

### 0.2.1-beta

In this version issue(s) have been fixed.

* When loading artwork fail, the app stop crashing

### 0.2.0-beta

In this version the album artwork is shown on the playback screen the music control bar has been
move
to the bottom of the screen.

## 0.1 (Android 14 Upside Down Cake and later)

### 0.1.0-beta

This version is the very first version of this app (in beta).
It includes basics functionalities of an mp3 player.
