# Simple Scrobbler

[<img src="https://github.com/simple-last-fm-scrobbler/sls/blob/master/art/icon_new.svg" alt="Icon" height="60">](https://github.com/simple-last-fm-scrobbler/sls/blob/master/art/icon_new.svg) [![GitHub release](https://img.shields.io/github/release/simple-last-fm-scrobbler/sls)](https://github.com/simple-last-fm-scrobbler/sls/releases/) [![License](http://img.shields.io/:license-apache-blue.svg?style=round)](LICENSE.md) [![Pull Requests](https://img.shields.io/github/issues-pr/simple-last-fm-scrobbler/sls.svg?style=round)](https://github.com/simple-last-fm-scrobbler/sls/pulls)

[![Feature Requests](https://img.shields.io/github/issues/simple-last-fm-scrobbler/sls/feature%20request.svg?style=round)](https://github.com/simple-last-fm-scrobbler/sls/labels/feature%20request) [![App Support Issues](https://img.shields.io/github/issues/simple-last-fm-scrobbler/sls/app%20support.svg?style=round)](https://github.com/simple-last-fm-scrobbler/sls/labels/app%20support) [![Bugs Issues](https://img.shields.io/github/issues/simple-last-fm-scrobbler/sls/bugs.svg?style=round)](https://github.com/simple-last-fm-scrobbler/sls/labels/bugs) [![Issues](https://img.shields.io/github/issues/simple-last-fm-scrobbler/sls.svg?style=round)](https://github.com/simple-last-fm-scrobbler/sls/issues)

> **Note :** Les modifications récentes de ce dépôt sont 100% vibe codé — générées et intégrées avec Claude Code sans intervention humaine dans le code.

## Nouveautés — v1.8.0

### Export scrobbles to CSV

Accessible via **Options → Export scrobbles (CSV)**, cette fonctionnalité permet d'exporter l'intégralité du cache local de scrobbles sous forme de fichier CSV directement sur l'appareil, sans dépendre d'aucun service externe.

 * **Colonnes exportées :** Date, Artist, Track, Album, Album Artist, Duration, Source, Rating
 * **Android 10+ (API 29+) :** fichier enregistré dans `Downloads/` via MediaStore (aucune permission requise)
 * **Android 5–9 :** stockage externe classique
 * Une notification affiche le chemin du fichier généré à la fin de l'export

---

Simple Scrobbler (SS) is a simple app that scrobbles music listened to on an Android phone. Scrobbling means submitting listening information to Last.fm (and optionally/additionally Libre.fm and ListenBrainz) when you play a track, and you can then get music recommendations and view your listening history and statistics at Last.fm.

 * More info about scrobbling can be found on [Last.fm's FAQ](http://www.last.fm/help/faq?category=Scrobbling#201).
 * More info about scrobbling from [Libre.fm's Project Page](https://git.gnu.io/foocorp/librefm/wikis/home).
 * More info about listens from [ListenBrainz's FAQ](https://listenbrainz.org/faq).

## Problem or Issue?

  * Please create a [GitHub Account](https://github.com/join) so you can create a [GitHub Issue](https://github.com/simple-last-fm-scrobbler/sls/issues) and PLEASE use the next sections as guides. You will get help with bugs and problems with the software. For direct personal contact with developers, please use [Discord](https://discordapp.com/channels/722441132260458496).
  * ### Issue (Bug Report) Example
    * **SLS Version (required)**: (1.8.0)
    * **Issue (required)**: (Brief description of your bug or issue)
    * **Parameters (required):**
      * **What**: {App Crashing, Scrobble Failure, or (you tell us)}
      * **Where**: {App Screen, Notifications, Hidden or (you tell us)}
      * **When**: {ex. "I clicked Scrobble Now", "I don't know", or (you tell us)}
      * **Why**: {ex. "Because I clicked Scrobble Now.", "I don't know.", or (you tell us)}
      * **How**: {ex. "Automatic", "Manual Button Click", "I don't know.", or (you tell us)}
    * *Android Version (preferred)*: (Android 12+)
    * *Music App(s) (preferred)*: (ex. Spotify, YouTube Music)
    * *Detected Music Apps(s) (preferred)*: (List what apps SLS detected)
    * *Enabled Settings (preferred)*: (List what options you have enabled)
    * Phone Model (optional): (Samsung S24)

  * ### Issue (Feature Request) Example
    * **Request for Feature (required)**: (control over scrobbled data)
    * **Reason (required)**: (Scrobble is love, scrobble is life.)

## Download

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
    alt="Get it on PlayStore"
    height="80">
    ](https://play.google.com/store/apps/details?id=com.adam.aslfms&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/com.adam.aslfms)


#### [Release & Pre-Release Downloads](https://github.com/simple-last-fm-scrobbler/sls/releases)

## Compatible Apps & Compatible Phones
### [(To View Compatible Apps and Phones Click Here)](Compatability.md)

**Note.** SLS should work on any device running Android 5.0 (API 21) or higher that can run one of the compatible music apps.

## Notices

 * SLS is maintained by a few dedicated programmers.
 * This app targets Android 16 (API 36) and requires Android 5.0 (API 21) minimum.

## Developers

 * Contributors are welcome!
 * Revisions will be requested if the changes are not working.
 * Please let us know if you have added strings that need translation in Pull Requests.

## Features

 * Scrobbling
 * Now playing notifications
 * Power-saving settings
 * Caching scrobbles (while offline or through settings)
 * Editing scrobble cache
 * Auto-correct scrobbles
 * Enable/Disable Music Apps
 * Themes
 * **Export scrobbles to CSV** *(new — see top)*

### Supported languages

 * English
 * Spanish
 * French
 * German
 * Brazilian Portuguese
 * Polish
 * Russian
 * Czech

### Supported scrobble services

## Last.fm, Libre.fm, ListenBrainz(.org), and Custom Personal Servers

### [![last.fm](art/last_fm.svg)](https://last.fm) [![libre.fm](art/libre_fm.svg)](https://libre.fm) [![ListenBrainz](art/ListenBrainz.svg)](https://listenbrainz.org)

####  there is also developer server options

 * [Custom GNU-fm/Libre.fm Server](https://git.gnu.io/gnu/gnu-fm/blob/master/gnufm_install.txt)
 * [Custom ListenBrainz Server](https://github.com/metabrainz/listenbrainz-server/blob/master/README.md)

### Changes

For a complete list of changes, see [the changelog](app/src/main/assets/changelog.txt).

## Bugs, Contributions, Thoughts...

First, read the [FAQ](FAQ.md) to see if you can find any help with your issue.

If you can't find it there, you can always open an issue or pull request here on GitHub.

## Credits

All of the code is open source, released under [Apache License 2.0](LICENSE.md).

 * Almost all of the code is written by me, so: Copyright 2009-2015 Adam Renberg.
 * The Last.fm logo is copyright of Last.fm, taken from their [media kit](http://www.last.fm/resources).
 * The Libre.fm logo is probably copyright of Libre.fm, used in good faith. (Because of their name and stated mission, I assume it is okay).

I use copyright here only in the sense of proper attribution. Do whatever you want with the code (as long as the licenses are followed).

### Contributors
#### A complete and updated list here -> [CONTRIBUTORS LIST](https://github.com/simple-last-fm-scrobbler/sls/graphs/contributors)

 * Adam Renberg, [github.com/tgwizard](https://github.com/tgwizard), main author
 * Argoday, [github.com/argoday](https://github.com/argoday), code fixes
 * inverse [github.com/inverse](https://github.com/inverse), core contributor
 * Austin H, [github.com/a93-39a](https://github.com/a93-39a), core contributor
 * Sean O'Neil, [github.com/SeanPONeil](https://github.com/SeanPONeil), android 4.0
 * Andrew Thomson, support for MIUI Music Player
 * Mark Gillespie, support for Sony/Sony Ericsson/Xperia phones
 * Dmitry Kostjuchenko, support for Neutron Music Player
 * stermi, support for Rdio
 * Joseph Cooper, [github.com/grodin](https://github.com/grodin), support for JRTStudio Android Music Player
 * Shahar, [github.com/kshahar](https://github.com/kshahar), support for LG Music Player
 * theli-ua, [github.com/theli-ua](https://github.com/theli-ua), support for Amazon Music
 * metanota, [github.com/metanota](https://github.com/metanota), support for PlayerPro, bug fixes
 * Joel Teichroeb, [github.com/klusark](https://github.com/klusark), bug fixes
 * Tha PHLASH, [thaphlash.com](http://www.thaphlash.com/), old icon
 * pierreduchemin [github.com/pierreduchemin](https://github.com/pierreduchemin), French translation
 * moshpirit [github.com/moshpirit](https://github.com/moshpirit), Spanish translation
 * bryansills [github.com/bryansills](https://github.com/bryansills), Eclipse to Android Studio, new icon, Material Design
 * herrherrmann [github.com/herrherrmann](https://github.com/herrherrmann), German translation
 * Alia5 [github.com/Alia5](https://github.com/Alia5), better Enabled apps handle
 * MendelGusmao [github.com/MendelGusmao](https://github.com/MendelGusmao), Brazil Portuguese Translation
 * Grzes58 [github.com/Grzes58](https://github.com/Grzes58), Polish
 * bajituka [github.com/bajituka](https://github.com/bajituka), Russian

### Test device contributors

 * Dmitry Paskal, [github.com/paskal](https://github.com/paskal)
 * Iļja Gubins, [https://github.com/the-lay](https://github.com/the-lay)

Several people have also contributed with comments, suggestions and [issues](https://github.com/simple-last-fm-scrobbler/sls/issues/).
