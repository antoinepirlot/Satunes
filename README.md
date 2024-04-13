# Lecteur MP3 (🇫🇷)

You can find this file in [english 🇬🇧](README-en.md).

MP3 Player est un lecteur de musique (lol). 
Tu peux l'utiliser pour écouter ta musique présente sur ton appareil Android. (Android 11 Red Velvet Cake et plus récent).
Cette application est en cours de développement et peut contenir des bugs et problèmes d'optimisations.

L'entierté du projet est sous la licence GNU/GPL v3 et s'applique à toutes les version de ce projet y compris le code ajouté avant l'introduction de la licence.

Tu as le droit de réutiliser mon code pour créer une nouvelle application dérivée mais tu devras utiliser la même licence ou une licence compatible.

# Installation

## Application

1) Clique sur [Release](https://github.com/antoinepirlot/MP3-Player/releases)
2) Choisi ta version
3) Clique sur "Assets" pour dérouler un menu qui contient le fichier d'installation
4) Clique sur le fichier MP3-Player_vx.y.z.apk (Un fichier va se télécharger sur ton appareil)
5) Une fois le téléchargement terminé, ouvre le fichier
6) Autorise l'installation d'application inconnue. (C'est nécéssaire car l'application n'est dépployée sur le Play Store).
7) Installe l'application (il est probable que tu doives recommencer l'étape 5).
8) Je te recommande te désactiver l'installation d'applis inconnue de l'application depuis laquelle tu as ouvert le fichier.
9) Profite de ta musique en toute liberté :D

## Android Auto

1) Va dans les paramètres de Android Auto.
2) Cliques plusieurs fois sur le bouton "Version" pour activer les paramètres développeurs.
3) Va dans les paramètres développeurs en cliquant sur les 3 boutons en haut à droite et active le paramètre "Sources Inconnues" car mon application n'est pas publiée dans le Google Play Store.
4) Il n'y a plus qu'à ajouter l'application au lanceur d'application de Android Auto dans le menu "Personnaliser le lanceur"
5) Bonne écoute et bon voyage ;)

##

Si l'application te plait et que tu as envie de me soutenir, n'hésite pas à le faire [ici](https://fr.tipeee.com/antoinepirlot).

# UPDATES

## 0.8 (Android 11 Red Velvet Cake et ultérieur)

### 0.8.1-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Lorsque l'utilisateur a ajouté des musique aux playlists mais qu'il décide de les supprimer de
  l'appareil. L'application supprime les musiques manquante des playlists. Cela empêche
  l'application de crasher à chaque lancement.
* Le système de mise à jour à été repensé. Maintenant, l'utilisateur doit cliquer sur un bouton dans
  les paramètres. Ce n'est plus automatique afin d'éviter des consomations de données inutiles.
* La vérification des mises à jour a été corrigée et prend maintenant en compte 4 types de mises à
  jour:
  * Alpha: Il s'agit des versions les plus expérimentales et les plus buguées. Les fonctionnalités
    ne sont pas marquée dans le marbre et peuvent disparaître avec une mise à jour ultérieure.
  * Beta: Il s'agit des versions de tests permettant de régler les problèmes des nouvelles
    fonctionnalités. Celle-ci seront ajouté définitivement à l'application (sauf cas de force
    majeur).
  * Preview: Il s'agit d'une version candidate qui, si tout ce passe bien, sera déployée en version
    stable.
  * Stable: Il s'agit d'une version prête à l'emploi. C'est la version recomandée pour une
    utilisation normale. Elle peut cependant encore présenter quelques bugs mais ceux-ci se feront
    rare et n'empêcheront pas le fonctionnement des fonctionalités.
* L'ajout de musiques à plusieurs playlists est en principe réglé.
* Les crash survenant lors d'une recomposition de l'UI suite à des changements comme "Rotation de
  l'écran" ou encore "Activation du mode sombre" ne feront plus crasher l'application.
* La navigation a été corigée, maintenant l'utilisateur peut revenir à d'autre écrans principaux
  depuis les paramètre suite à une recompositions de l'écran comme indiqué au point précédent.
* Lors du lancement de l'application, elle ne devrait plus crasher suite à des problèmes liés à
  l'initialisation du playback.
* La structure des données a été révisée afin de régler certains problèmes comme l'ouverture d'un
  album qui n'était pas le bon ou qui faisait crasher l'application lorsque 2 albums ont le même
  nom.
* Optimisation du chargement des données ainsi que du changement du mode aléatoire (grâce aux
  coroutines de Kotlin)
* La barre de progression a été réduite sur les côtés pour éviter un conflit avec la navigation par
  geste.
* Le playback dans Android Auto a été réglé. Cela se traduira par une possible augmentation de
  l'utilisation de la batterie mais ceci n'a pas encore pu être vérifié.

### 0.8.0-beta

Nouvelles Fonctionnalités:

* Il est possible de vérifier si une mise à jour est disponible via les paramètres.
* Ajout de nouvelles sections dans la page paramètres.
* Ajout de titre indiquant où l'utilisateur se trouve.
* Ajout d'une liste horizontale affichant les albums d'un artiste.
* Possibilité de cliquer sur l'album (dans la page d'un artiste ou depuis la musique en cours de lecture) pour afficher la page de l'album.

Améliorations, Optimisations et Résolutions de problèmes:

* Le paramètre qui permet de masquer l'onglet fichier est réparé, le paramètre est enregistré correctement.
* L'affichage de la page paramètre à été repensé et amélioré.

## 0.7 (Android 11 Red Velvet Cake et ultérieur)

### 0.7.3-beta

Améliorations, Optimisation et Résolution de problèmes:

* Le chargement des données a été optimisé et est beaucoup plus rapide.
* Tous les textes affichés dans Android Auto sont maintenant déchappés pour voir les bons
  caractères.

### 0.7.2-beta

Améliorations, Optimisation et Résolution de problèmes:

* La navigation entre les pages est plus intuitive
* Correctif apporté au playback
* Les caractères spéciaux sont échappés, cela règles des problèmes d'ouverture de dossier/media avec
  un nom contenant un caractère spécial (par exemple: Hip-Hop/Rap)

### 0.7.1-beta

Maintenant, l'application possède son icône 🤗 (notez que je ne suis pas un artiste 🤭).

Résolution des problèmes:

* Le bouton pour créer une playlist est maintenant situé en bas à droite de l'écran.
* Le playback a été remanié et les changements de pistes ne devraient faire de problème avec
  l'affichage de la musique en cours de lecture.
* Un cercle de chargement apparaît au lancement lorsqu'il y a beaucoup de musiques à charger afin d'
  éviter un lancement plus lent de l'application.
* La musique ne se met plus en pause automatiquement dans Android Auto lors d'un changement de
  musique depuis la liste de lecture.

### 0.7.0-beta

Fonctionnalités:

* Ajout des playlists

## 0.6 (Android 11 Red Velvet Cake et ultérieur)

### 0.6.0-beta

Cette version rend l'application compatible avec Android 11 Red Velvet Cake et ultérieur.

## 0.5 (Android 14 Upside Down Cake et ultérieur)

### 0.5.1-beta

Résolutions de problèmes:

* Optimisation de Android Auto
* La lecture de la musique est correctement synchronisée.

### 0.5.0-beta

Fonctionnalités:

* Support pour Android Auto
    * Pour activer Android Auto clique [ici](#android-auto) pour savoir comment faire.

Résolutions de problèmes:

* Optimisation
* Les infrmations de la musique sont affichée dans la notification.

## 0.4 (Android 14 Upside Down Cake et ultérieur)

### 0.4.2-beta

Résolutions de problèmes:

* La musique est mise en pause lorsqu'une autre application mets de l'audio.

### 0.4.1-beta

Résolutions de problèmes:

* Musiques, Albums, etc. sont triées par rapport à leurs noms.

### 0.4.0-beta

Fonctionnalités:

* Affichage de la lite des genres et possiblité de lire la musique depuis ceux-ci.
* Paramètres
    * Authorise l'utilisateur à choisir d'arrêter la lecture si l'application est fermée depuis le multi-âche.
    * Authorise l'utilisateur à choisir d'arrêter la lecture si le casque/écouteurs sont déconnectés.

Résolutions de problèmes:

* La musique provenant d'un stockage externe peut être lue.
* Affiche les différents stockage ("Cet appareil", "Carte SD", etc.)

## 0.3 (Android 14 Upside Down Cake et ultérieur)

### 0.3.2-beta

Résolutions de problèmes:

* Désactive la mise en aléatoire depuis l'extérieur de l'application.

### 0.3.1-beta

Résolutions de problèmes:

* Le chargement des données sur les appareils Galaxy ne font plus crasher l'application si aucune image d'album n'est trouvée.
* La déconnexion des écouteurs/casques met la lecture en pause.
* Le bouton play/pause est correctement synchronisé avec l'état de la musique.

### 0.3.0-beta

De nouvelles fonctionnalités sont arrivées:

* Affichage des albums et la possibilité de lire la musique depuis ceux-ci.
* Page des paramètres
    * Possiblité de modifier la bar en bas de l'écran en enlevant des catégories.

## 0.2 (Android 14 Upside Down Cake et ultérieur)

### 0.2.1-beta

Dans cette version, le problème suivant à été resolu:

* Lors du chargement des images d'albums, l'application ne crash plus.

### 0.2.0-beta

Dans cette version, l'image de l'album est montré dans la vue lecture en cours et la bar de progression à été déplacée en bas de l'écran.

## 0.1 (Android 14 Upside Down Cake et ultérieur)

### 0.1.0-beta

Cette version est la toute première version de l'application (en bêta).
Cela inclus les fonctionnalités de base d'un lecteur MP3.
