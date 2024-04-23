# Releases (🇬🇧)
Tu peux retrouver ce fichier en [français 🇫🇷](RELEASES_FR.md)

## 0.9 (Android 9 Pie and later)

### 0.9.1-beta

Improvements, Optimizations and Troubleshooting:

* Changed domain name to "io.github.antoinepirlot.satunes".
* The design has been reworked to bring default album art throughout the application.
* Album art displays in Android Auto.
* Database migrations can be done.
* Fixed some issues when initializing Android Auto in some cases.
* Screen recomposition when adding data such as playlists is now operational.

### 0.9.0-beta

MP3 Player finally has its own name "Satunes". This version becomes compatible with Android 9 Pie and newer.

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
  * Alpha: These are the most experimental and buggy versions. Features are not set in stone and may
    disappear with a subsequent update.
  * Beta: These are test versions to resolve problems with new features. This will be permanently
    added to the application (except in cases of major forces).
  * Preview: This is a release candidate which, if all goes well, will be deployed as a stable
    version.
  * Stable: This is a ready-to-use version. This is the recommended version for normal use. However, it may still present some bugs but these will be fixed
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
* Possibility to click on album (in the artist page or from the currently playing music) to display the album page.

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
    * To activate Android Auto you can find instructions [here](#android-auto)

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
