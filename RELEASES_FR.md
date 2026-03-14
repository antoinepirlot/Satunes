# Versions (🇫🇷)

You can find this file in [english 🇬🇧](RELEASES_EN.md)

## 3.3 (Android 5.1.1 Lollipop et ultérieur)

### 3.3.4

Améliorations, optimisations et corrections de bogues :

* Correction d'un problème lié à l'exportation/importation des playlists. Désormais, dans un fichier m3u, les chemins d'accès se présentent comme suit : /storage/[storag_device]/path/to/music.extension. Ne vous inquiétez pas, si vous disposez déjà d'anciens fichiers dont les chemins commencent par « file:/// », Satunes les prend toujours en charge.
* Ajout d'un message après la boîte de dialogue « Nouveautés » pour indiquer que la version 4.0.0 nécessitera Android Marshmallow (6.0) pour fonctionner.
* Correction d'un problème lors de la mise à jour d'une playlist qui entraînait la suppression de certains morceaux de la liste s'ils n'étaient pas désélectionnés.

### 3.3.3

Améliorations, optimisations et corrections de bugs :

* Si le fichier d'exportation n'a pas pu être créé, Satunes arrête l'exportation
* Correction de l'exportation d'une seule playlist sous le format m3u

### 3.3.2

Améliorations, optimisations et corrections de bugs :

* Correction de la navigation lorsque, dans certaines conditions, l'icône de liste ne s'affiche pas
* Correction du plantage si un fichier musical n'est pas chargé correctement

### 3.3.1

Améliorations, optimisations et corrections de bogues :

* Correction du nom des dossiers en supprimant "Stockage externe: " pour les sous dossiers.
* Correction de l'action du dossier "../"

### 3.3.0

Nouvelles fonctionnalités :

* Lors du tri des fichiers multimédias dans un autre type de média, le titre de l'autre média s'
  affiche à la place de la première lettre.
  (Par exemple, lorsque tu tries la musique par albums, le titre de l'album s'affiche pour chaque
  album.)
* Ajout d'un dossier « .. » pour revenir en arrière
* Nouveau paramètre dans la bibliothèque pour choisir si le titre de la musique est le nom du
  fichier ou le titre dans les métadonnées.
* Option pour inverser le tri
* Le chemin d'accès dans la vue du dossier est cliquable pour naviguer

Améliorations, optimisations et corrections de bogues :

* Correction de la vérification des mises à jour sur d'autres canaux que le canal stable.
* Correction du plantage du tri par date
* Ajout d'un espace entre le formulaire et le bouton dans la boîte de dialogue du formulaire de
  minuterie
* Mise à jour de la conception de la liste des médias
* Correction des options de tri lorsque de la sortie de la boîte de dialogue de tri sans appliquer
  de modification, les valeurs précédentes sont
  restaurées
* Correction de la modification de l'avance et de la récompense qui prend effet immédiatement
* Correction du retour à l'affichage par défaut lors de la rotation ou du changement de mode
  clair/foncé
* Quelques optimisations
* Mise à jour des bibliothèques

### 3.3.0-preview-2

Améliorations, optimisations et corrections de bugs :

* Correction des boutons des vues multimédia qui s'affichent là où ils ne devaient pas
* Affichage de « Stockage externe : [nom du stockage] » dans la vue des dossiers pour les stockages
  tels que les cartes SD
* Correction du retour à la vue par défaut lors de la rotation ou du changement de mode clair/foncé

### 3.3.0-preview-1

Améliorations, optimisations et corrections de bugs :

* Cliquer sur le bouton « chemin » permet désormais d'avancer au lieu de revenir en arrière
* Correction des modifications apportées à l'avance et aux recul, elles prennent directement effet
* Correction du crash lors de la rotation de l'écran
* Correction de la mise en surbrillance incorrecte de la barre de navigation dans certaines vues
* Corrections éventuelles d'autres bugs inconnus

### 3.3.0-beta-2

Nouvelles fonctionnalités :

* Le chemin d'accès dans la vue des dossiers est cliquable pour naviguer

Améliorations, optimisations et corrections de bogues :

* Correction de la vérification des mises à jour sur d'autres canaux que le canal stable.
* Suppression du séparateur de lettres dans la vue de la liste de lecture et la vue de l'album
* Correction du plantage du tri par date
* Ajout d'un espace entre le formulaire et le bouton dans la boîte de dialogue du formulaire de
  minuterie
* Correction des options de tri lorsque vous quittez la boîte de dialogue de tri sans appliquer les
  modifications, les valeurs précédentes sont restaurées
* Les musiques stockées sur des espaces de stockage externes n'apparaissent plus dans le premier
  dossier à côté du dossier « Cet appareil »
* Amélioration du design de la liste
* Plus aucun dossier associé au nom de fichier créé
* Le dossier précédent peut être cliqué une fois par affichage (cela empêche d'accéder à un
  affichage vide si vous cliquez trop rapidement)

### 3.3.0-beta-1

Nouvelles fonctionnalités :

* Lorsque vous triez des médias dans un autre type de média, le titre de l'autre média s'affiche à
  la place de la première lettre. (Par exemple, lorsque vous triez la musique par albums, le titre
  de l'album s'affiche pour chaque album.)
* Mise à jour de l'interface de la liste des médias
* Ajout d'un dossier ".." pour revenir en arrière
* Nouveau paramètre dans la bibliothèque pour choisir si le titre de la musique est le nom du
  fichier ou la balise dans les métadonnées.
* Nouvelle option pour inverser le tri

Améliorations, optimisations et corrections de bogues :

* Quelques optimisations
* Mise à jour des librairies

## 3.2 (Android 5.1.1 Lollipop et ultérieur)

### 3.2.3

Améliorations, optimisations et corrections de bugs :

* Correction de l'action de la bannière de l'artiste dans la vue de l'album.
* Correction de l'export d'une seule playlist à partir de ses options. Maintenant l'option exporte
  uniquement cette playlist.

### 3.2.2

Améliorations, optimisations et corrections de bugs :

* Rétablissement de Satunes sur Android 5.1.1 (il s'agissait d'une erreur, mes excuses)

### 3.2.1

Améliorations, optimisations et corrections de bugs :

* Correction d'un problème lors de l'ouverture de musique à partir de l'explorateur de fichiers sur
  les appareils Samsung.

### 3.2.0

Nouvelles fonctionnalités :

* Possibilité d'ouvrir un fichier audio à partir de l'explorateur de fichiers.
* Support du format M3U pour les playlistes (voir importation/exportation).

Améliorations, optimisations et corrections de bogues :

* Correction de la vérification des mises à jour.
* Suppression du bouton « Artiste » dans la boîte de dialogue « Album » si l'affichage actuel est
  celui de l'artiste.

### 3.2.0-preview-3

Améliorations, optimisations et dépannage :

* Correction du bug ne pouvant ouvrir un dossier
* Correction du problème lors de l'ouverture d'une musique ne faisant pas partie des musiques
  chargées
* Correction de la vérification d'une mise à jour

### 3.2.0-preview-2

Améliorations, optimisations et dépannage :

* Suppression du bouton « Artiste » dans la boîte de dialogue « Album » si l'affichage actuel est
  celui de l'artiste.
* Correction d'un plantage lors de l'ouverture d'un fichier musical à partir de l'explorateur de
  fichiers alors qu'une lecture est déjà en cours et que le fichier musical n'est pas dans la liste
  de lecture.
* Correction permettant l'ouverture du même fichier musical dans la session.

### 3.2.0-preview-1

Nouvelles fonctionnalités :

* Possibilité d'ouvrir un fichier audio depuis l'explorateur de fichier

### 3.2.0-beta-1

Nouvelles fonctionnalités :

* Support du format M3U pour les playlistes (voir importation/exportation).

## 3.1 (Android 5.1.1 Lollipop et ultérieur)

### 3.1.3

Améliorations, optimisations et dépannage :

* Correction du crash lors du tri dans la vue d'un dossier

### 3.1.2

Améliorations, optimisations et dépannage :

* Correction des boutons de lecture qui ne s'affichent pas dans les vues de dossiers
* Correction du geste de retour avec la navigation par balayage (le geste de retour prédictif est
  temporairement désactivé)

### 3.1.1

Améliorations, optimisations et dépannage :

* Correction de l'interface utilisateur des paramètres après la réinitialisation de certains
  paramètres
* Clarification du nom de l'option de tri de l'ordre d'ajout à une playlist
* Correction de l'ordre de la musique lue à partir d'un dossier en mode non aléatoire
* Correction du rafraîchissement du widget disque dans certaines conditions

### 3.1.0

Nouvelles fonctionnalités :

* Nouveau Widget "Disque"
* Réglages pour arrondir les albums et animer les illustrations lors de la lecture
* La fonction inclure/exclure prend désormais en compte les deux cas. Tu peux maintenant inclure
  certains dossiers et en exclure d'autres en même temps.
* Bouton pour désactiver les logs
* Ajout des abonnements aux canaux de mises à jour

Améliorations, optimisations et dépannage :

* Correction du chemin trop long qui cache l'icône de la corbeille dans les paramètres de la
  bibliothèque
* Correction d'un crash lors de la lecture de musique après autorisation de l'audio
* Cacher les boutons pour jouer de la musique dans la vue des médias pour éviter de jouer de la
  musique pendant le chargement des données
* Lors de l'ajout à la file d'attente, les musiques sont ajoutées dans un ordre aléatoire si le mode
  aléatoire par défaut est activé.

### 3.1.0-preview-1

Nouvelles fonctionnalités :

* Ajout des abonnements aux canaux de mises à jour

Améliorations, optimisations et dépannage :

* Correction de la fenêtre d'information lors de l'ouverture des paramètres de la bibliothèque, elle
  sera maintenant cachée de façon permanente lorsque le bouton "ok" sera cliqué
* L'interrupteur d'inclusion/exclusion est de nouveau un simple bouton (plus d'action d'
  interrupteur)
* Correction du chemin trop long qui cache l'icône de la corbeille dans les paramètres de la
  bibliothèque
* L'image de l'album peut être arrondie dans Android Auto
* Correction de la zone cliquable de l'illustration circulaire dans la vue de lecture (c'est
  maintenant un cercle)
* Tentative de correction du chargement du mauvais widget

### 3.1.0-beta-1

Nouvelles fonctionnalités :

* Nouveau Widget "Disque"
* Réglages pour arrondir les albums et animer les illustrations lors de la lecture
* La fonction inclure/exclure prend désormais en compte les deux cas. Tu peux maintenant inclure
  certains dossiers et en exclure d'autres en même temps.
* Bouton pour désactiver les logs

Améliorations, optimisations et dépannage :

* Correction d'un crash lors de la lecture de musique après autorisation de l'audio
* Cacher les boutons pour jouer de la musique dans la vue des médias pour éviter de jouer de la
  musique pendant le chargement des données
* Lors de l'ajout à la file d'attente, les musiques sont ajoutées dans un ordre aléatoire si le mode
  aléatoire par défaut est activé.

## 3.0 (Android 5.1.1 Lollipop et ultérieur)

### 3.0.5

Améliorations, optimisations et dépannage :

* Remplacement du bouton tipeee par Liberapay

### 3.0.4

Améliorations, optimisations et dépannage :

* Correction de la position de la liste lors du changement de tri
* Les toasts sont centrés ainsi que les boutons dans les paramètres
* Suppression des lignes de séparation ajoutées par erreur dans la pop-up de sélection des médias

### 3.0.3

Améliorations, optimisations et dépannage :

* Correction du crash lors de l'utilisation d'une carte SD

### 3.0.2

Améliorations, optimisations et dépannage :

* Amélioration du comportement des listes
* Correction des paramètres de media (retour à l'ancien système qui était plus efficace)
* Correction de la fonction d'exclusion de dossiers
* Suppresion de la permission inutile "manage_external_storage"
* Correction de la navigation :
    * Si l'audio n'est pas autorisé, les paramètres sont accessibles.
    * Si l'audio vient d'être autorisé dans l'app, la navigation est alors pleinement fonctionnelle

### 3.0.1

Améliorations, optimisations et dépannage:

* Correction d'un crash lors du chargement d'une carte SD. Le tri par date de dernier ajout est
  temporairement indisponible pour les fichiers stockés sur des cartes SD.

### 3.0.0

Nouvelles fonctionnalités :

* Possibilité de trier les médias par artistes, album et/ou genre en fonction de la page
* Affichage de la première lettre par ordre alphabétique au dessus du premier média (ayant cette
  lettre) dans la liste (désactivable dans les paramètres de librairies)
* Ajout du tri par années et dernier ajout pour les musiques (dans certaines vues pour le moment)
* L'ajout des médias dans les playlists permet maintenant de les supprimer s'ils y sont présents (
  entièrement pour ceux qui ne sont pas des musiques)
* Il est possible de déplacer des positions d'actions personnalisées
* Une playlist peut être sélectionnée comme onglet par défaut

Améliorations, optimisations et dépannage:

* Support pour Android 15 (Vanilla Ice Cream)
* Problèmes de chargement de la musique causant des crashs dans certaines conditions corrigé (
  Concurrent Modification)
* Mise à jour des textes
* Mise à jour des libraries qui implique un léger changement de design de certains éléments (commme
  la barre de progression)
* Lorsque l'utilisateur change de vue d'un type de média à un autre, la barre de navigation reflète
  le changement
* Autres optimisations et corrections indirectes
* Changement de la source de la mise à jour de GitHub vers Codeberg
* Remplacement du bouton GitHub par Codeberg

### 3.0.0-preview-1

Améliorations, optimisations et dépannage :

* Correction du problème de la selection de playlist qui s'affichait en allant dans la vue playback
* Démarrage plus rapide
* Correction du clique sur la musique dans la liste du playback
* La liste des media ne retourne pas au premier élément si les options du media se ferment
* Correction de problème liés à la navigation
* Centrage de l'icône artiste à côté du nom d'artiste
* Changement de la source de la mise à jour de GitHub vers Codeberg
* Remplacement du bouton GitHub par Codeberg

### 3.0.0-beta-2

Améliorations, optimisations et dépannage :

* Correction de quelques problèmes d'interface utilisateur
* Mise à jour des textes
* Suppression de certaines actions d'annulation lors de la modification des playlists car c'est plus
  complexe maintenant
* Correction d'un plantage lors du chargement d'une musique non existante (oui cela peut arriver
  avec MediaStore lol)
* Correction de l'ouverture de la playlist par défaut
* Réinitialisation de l'ancien ordre de la liste des boutons extra dans la vue d'une playlist
* Correction de la vue recherche
* Autres optimisations et améliorations indirectes

### 3.0.0-beta-1

Nouvelles fonctionnalités :

* Il est possible de déplacer des positions d'actions personnalisées
* Une playlist peut être sélectionnée comme onglet par défaut.

Améliorations, optimisations et dépannage :

* Mise à jour des textes
* Ajout d'une icône artiste à côté du titre de l'artiste dans certaines vues
* Mise à jour de la position des éléments dans la vue de mise à jour
* Déplacer les paramètres dédiés à au design dans les paramètre "Design"
* Mise à jour de la structure du code
* Correction de certains problèmes d'interface utilisateur depuis la version 3.0.0-alpha-2
* Autres optimisations et améliorations indirectes

### 3.0.0-alpha-2

Nouvelles fonctionnalités:

* Ajout du tri par années et dernier ajout pour les musiques (dans certaines vues pour le moment)
* L'ajout des médias dans les playlists permet maintenant de les supprimer s'il y sont présents (
  entièrement pour ceux qui ne sont pas des musiques)

Améliorations, optimisations et dépannage:

* Optimisation du démarrage
* Lorsque la liste est triée, la liste chargée en playback correspond au tri
* Lorsque l'utilisateur change de vue d'un type de média à un autre, la barre de navigation reflète
  le changement
* Autres optimisations et améliorations indirects

### 3.0.0-alpha-1

Nouvelles fonctionnalités:

* Possibilité de trier les médias par artistes, album et/ou genre en fonction de la page
* Affichage de la première lettre par ordre alphabétique au dessus du premier média (ayant cette
  lettre) dans la liste (désactivable dans les paramètres de librairies)

Améliorations, optimisations et dépannage:

* Support pour Android 15 (Vanilla Ice Cream)
* Problèmes de chargement de la musique causant des crash dans certaines conditions corrigé (
  Concurrent Modification)
* Mise à jour des libraries qui implique un léger changement de design de certains éléments (commme
  la barre de progression)
* Autres optimisations et corrections indirectes

## 2.5 (Android 5.1.1 Lollipop et ultérieur)

### 2.5.2

Améliorations, optimisations et dépannage:

* Le bouton démarrer le minuteur est masqué lorsque le minuteur fonctionne

### 2.5.1

Améliorations, optimisations et dépannage:

* Ajout du text "Développé en Europe 🇪🇺" à la fin de la section à propos dans les paramètres
* La snack bar ne montre plus le caractère '%' lors de la suppression d'un chemin de dossier
* La barre de contrôle de la musique est toujours à jour même en quittant et en revenant dans la
  page lors de la lecture
* Si la nouvelle valeur pour avancer ou revenir en arrière est <= 0, alors la valeur n'est pas
  changée
* Une popup d'information est montrée si le paramètre nécessite un rechargement de la bibliothèque

### 2.5.0

Nouvelles fonctionnalités:

* Ajout des musiques d'une playlist vers une autre
* Avancer ou reculer dans une musique
* Réinitialiser les paramètres

Améliorations, optimisations et dépannage:

* La vue du minuteur montre le temps restant dans les champs.
* Quelques optimizations mineures

## 2.4 (Android 5.1.1 Lollipop et ultérieur)

### 2.4.6

Améliorations, optimisations et dépannage:

* Correction du bogue d'Android Auto qui ne lit pas la musique séléctionnée et qui ne respecte pas
  le mode aléatoire par défaut

### 2.4.5

Rien de nouveau. Une erreur est survenue lors de la mise à jour

### 2.4.4

Améliorations, optimisations et dépannage:

* Suppression de l'exclusion des bibliothèques open source
* Désactivation de l'inclusion des métadonnées de dépendances dans les APK et Bundle car ce n'est
  pas une méthode compatible avec l'open source

### 2.4.3

Améliorations, optimisations et dépannage:

* Le bouton pour afficher la liste de lecture est agit comme un switch (comme le bouton paramètre)
* Correction du problème empêchant l'accès à une vue d'un média lorsque le bouton de la barre de
  navigation correspondant est désactivé
* Le widget peut être redimensionné verticalement mais est limité (cela empêche des problème
  d'affichage avec certain launcher)

### 2.4.2

Améliorations, optimisations et dépannage:

* L'ajout de musiques dans les playlists fonctionne à nouveau

### 2.4.1

Améliorations, optimisations et dépannage:

* Lors de l'annulation de la sélection des médias, ceux sélectionné ne sont plus enregistrés
* Lors de la sélection du menu bar défaut, l'application ne redirige plus vers le nouveau menu

### 2.4.0

Nouvelles fonctionnalités:

* Possibilité de liker la musique dans Android Auto
* Minuteur pouvant mettre la musique en pause (maximum pendant 8h)

Améliorations, optimisations et dépannage:

* La lecture de la musique en cours depuis la même playlist chargée ne recharge pas la lecture
* Les musiques sont triées par numéro de piste dans les albums puis, sinon, par ordre alphabétique
* Le nom de l'exportation du fichier de playlist et logs contiennent une date et une heure
* Amélioration du refresh du widget
* L'année de l'album est affichée, si elle existe
* Les boutons de mises à jours sont mit en colonne pour améliorer leur visibilité sur certains
  écrans
* Le bouton like dans Android Auto a été échangé avec le bouton shuffle
* Correction du changement de piste vers la précédente dans certaines conditions

Fonctionnalités supprimées:

* Suppression de la visualisation de la liste de lecture dans Android Auto pour éviter les
  distractions

### v2.4.0-preview-1

Améliorations, optimisations et dépannage:

* Correction des bogues de la v2.4.0-beta-1
    * Crash de la lecture de la musique en cours de lecture
    * Ajout d'une musique dans une playlist
    * Montre à nouveau le bouton pour supprimer la musique de la playlist
    * Crash lors du démarrage sur Android Q et antérieur

### 2.4.0-beta-1

Nouvelles fonctionnalités:

* Possibilité de liker la music dans Android Auto
* Minuteur pouvant mettre la musique en pause (maximum pendant 8h)

Améliorations, optimisations et dépannage:

* La lecture de la musique en cours depuis la même playlist chargée ne recharge pas la lecture
* Les musiques sont triées par numéro de piste dans les albums puis, sinon, par ordre alphabétique
* Le nom de l'exportation du fichier de playlist et logs contiennent une date et une heure
* Amélioration du refresh du widget
* L'année de l'album est affichée, si elle existe
* Les boutons de mises à jours sont mit en colonne pour améliorer leur visibilité sur certains
  écrans

Fonctionnalités supprimées:

* Suppression de la visualisation de la liste de lecture dans Android Auto pour éviter les
  distractions

## 2.3 (Android 5.1.1 Lollipop et ultérieur)

### 2.3.3

Améliorations, optimisations et dépannage:

* Correction du bug d'affichage innatendu causé par Android 15 en rétrogradant targetSdk vers
  Android 14

### 2.3.2

Améliorations, optimisations et dépannage:

* Le rafraichissement du widget a été amélioré
* Retour du padding horizontal pour les textes dans la vue du playback

### 2.3.1

Améliorations, optimisations et dépannage:

* Suppression de la limite d'Android Auto
* Par défaut, Satunes charge l'artiste de la musique s'il n'y a pas d'artiste de l'album
* Le chargement de la bibliothèque n'affichera pas de message d'avertissement
* Le nettoyage des playlists affichera un message d'avertissement
* Modification de certains textes
* Le titre de la playlist n'acceptera pas les retour à la ligne ou les tabulations
* La zone de défilement des rangées de boutons n'est plus coupée
* La taille du fichier journal est de 5MB maximum et suppression des logs lors de la mise à jour de
  la barre de progression pour optimiser l'utilisation de la batterie.
* Correction du mauvais mode de répétition après le rechargement de la bibliothèque
* Rendre la vue des paramètres de recherche déroulante
* Correction de la lecture de la musique suivante dans certaines conditions

### 2.3.0

Nouvelles fonctionnalités:

* Possibilité d'activer les modes aléatoire et répétition avec la notification
* Partager des fichiers multimédias
* Un clic long sur la pochette de l'album ouvre les options de l'album
* Un clic long sur le texte de l'artiste dans la vue de lecture ouvre les options de l'artiste
* Possibilité de charger l'artiste de la musique si l'artiste de l'album est inconnu (paramètres de
  la bibliothèque)
* Nettoyer les playlists pour supprimer la musique enregistrée si elle n'existe plus dans les
  musiques chargées au lieu de le faire automatiquement

Améliorations, optimisations et dépannage:

* Correction de l'importation d'une seule liste de lecture
* Correction de l'importation d'une liste de lecture lorsqu'elle existe déjà
* Quelques optimisations
* Ajout du bouton Fdroid dans la section des paramètres
* Les paramètres de la liste de lecture ont été déplacés vers les paramètres de la bibliothèque
* Correction de la lecture suivante dans certaines conditions
* Correction de la suppression de musiques de la file d'attente dans certaines conditions
* Correction du crash lors du lancement sur Android 9 et versions antérieures si l'autorisation de
  lecture du stockage externe n'est pas autorisée

## 2.2 (Android 5.1.1 Lollipop et ultérieur)

### 2.2.4

Améliorations, optimisations et dépannage:

* Si les fichiers ont une taille de 0 ou une durée de 0, ils ne seront pas chargés
* Si la vue de lecture souhaite afficher une valeur NaN Float pour la position actuelle, elle
  affichera 00:00 au lieu de faire planter l'application

### 2.2.3

Améliorations, optimisations et dépannage:

* Désactivation des boutons de navigation s'ils ont été désactivé.
* La section par défaut ne peut pas être une section de barre de navigation désactivée
* Correction d'orthographe dans une chaîne de caractère en français
* Ajout d'un espace entre le texte du contenu et les boutons dans les paramètres des playlists

### 2.2.2

Nouvelles fonctionnalités:

* Possibilité d'annuler la suppression d'un chemin

Améliorations, optimisations et dépannage:

* Ajout de retour haptique lors de clique sur les boutons rapide
* Correction de l'affichage des chemins, maintenant centré
* Modification de quelques textes
* L'ouverture de l'application prendra toujours le paramètre de section par défaut en compte
* La musique suivante peut être supprimée de la liste de lecture
* Les genres affichent de nouveau les albums
* Lorsque une musique précendent la musique en cours est mise en suivante, la liste reste
  synchronisée avec l'ordre

### 2.2.1

Améliorations, optimisations et dépannage:

* Correction du crash sur les versions Android 10 (Quince Tart) et plus anciennes
* Mise à jour de quelques chaînes de caractères

### 2.2.0

Nouvelles fonctionnalités:

* Prise en charge des compilations
* Widget classique
* Sélection de la section de barre de navigation par défaut

Améliorations, optimisations et dépannage:

* Le paramètre "Dossiers" devient "Bibliothèque"
* Optimisation du démarrage d'Android Auto
* La surbrillance de la section de la barre de navigation est masquée dans les vues paramètres,
  playback et recherche
* Correction d'un crash lorsque Satunes n'a pas été utilisé pendant un moment et est réouvert à
  partir du multitâche
* L'option "Lire ensuite" est masquée dans les options de la musique suivante
* Quelques autres optimisations

## 2.1 (Android 5.1.1 Lollipop et ultérieur)

### 2.1.2

Améliorations, optimisations et dépannage:

* Ajout d'une nouvelle ligne à la fin de la ligne de log
* Correction de l'absence de chemin sélectionné et du paramètre d'inclusion sélectionné

### 2.1.1

Améliorations, optimisations et dépannage:

* Correction des problèmes d'Android Auto
* Correction du plantage de l'application à chaque lancement de l'application avec Android Auto
* Chemin:
    * Afficher "cet appareil" au lieu de "/0"
    * Déplacement du bouton d'actualisation vers une nouvelle ligne
* Optimisation du processus de recherche

### 2.1.0

Nouvelles fonctionnalités:

* Il est désormais possible d'inclure/exclure des chemins
* Exporter une seule playlist est de retour
* Supprimer un média de la file d'attente
* Lire ensuite et ajouter à la file d'attente pour tous les médias
* Une fois que la ou les musiques ont été ajoutée(s) à la ou aux playlist(s), le snackBar dispose
  d'une action d'annulation. De même, après l'annulation, tu peux annuler l'annulation mdr

Améliorations, optimisations et dépannage:

* L'utilisation de la RAM a été considérablement réduite, les illustrations sont chargées lorsque
  cela est nécessaire
* La lecture est plus stable lorsqu'elle est relachée

## 2.0 (Android 5.1.1 Lollipop et ultérieur)

### 2.0.1

Améliorations, optimisations et dépannage:

* Il n'est plus possible d'avoir 2 playlists différentes de même nom peu importe les
  majuscules/minuscules
* Les espaces sont retirés lors de la création d'une playlist ou de la modification de son titre
* Lors du lancement de la lecture via les recherches avec le mode aléatoire, la musique sélectionnée
  est la première de la liste de lecture
* Lors d'une recherche, si les premiers ou derniers caractères sont des espaces, ils sont ignorés

### 2.0.0

Améliorations, optimisations et dépannage:

* Restructuration du code
* Amélioration des performances et de la rapidité de chargement et d'accès aux données
* Android Auto est plus stable bien qu'Android Auto a quelques contrainte en terme de quantité de
  donnée
* L'importation et exportation des playlists fonctionne correctements
* La barre de progression affichera les niveaux prédéfinis dans cet ordre:
    * Très lent
    * Lent
    * Un peu lent
    * Normal
    * Rapide
    * Très rapide
    * En temps réel
* Définition d'un ordre pour les médias si leur titre est exactement le même lors de la comparaison,
  à l'écran si cela se produit, tu verras les médias dans cet ordre:
    * Musique
    * Album
    * Artiste
    * Genre
    * Playlist
    * Dossier
* Utilisation des snack bar pour les différentes notifications
* Ajout du titre de l'album sur l'élément musical pour différencier les albums et les musiques
* Les boutons des réglages aléatoire ne sont plus des commutateurs mais agissent comme les boutons
  pour le mode de répétition
* Désormais, l'application chargera même vos musiques dupliquées
* Par défaut, Satunes ne chargera que le dossier principal Musiques et pas la totalité.
* Satunes enregistre certaines erreurs via des logs (sans informations personnelles), tu pouvez les
  exporter, rien ne quittera l'application sans votre autorisation.
* Désormais, la lecture de musique à partir d'un dossier chargera d'abord ses musiques, puis celles
  de ses sous-dossiers, toujours triées par titre.
* Ajout d'icônes pour identifier les paramètres plus facilement.

### 2.0.0-preview-1

Améliorations, optimisations et dépannage:

* Utilisation des snack bar pour les notifications
* L'importation/exportation de playlists a été corrigé et fonctionne désormais comme prévu
* Refactorisation de Android Auto, j'ai essayé de le rendre plus stable et de résoudre les
  différents problèmes
* Ajout du titre de l'album sur l'élément musical pour différencier les albums et les musiques
* Correction de la fermeture de l'application lors de la lecture lorsque le paramètre pour la
  maintenir en mode lecture désactivé
* La barre de progression affichera les niveaux prédéfinis dans cet ordre:
    * Très lent
    * Lent
    * Un peu lent
    * Normal
    * Rapide
    * Très rapide
    * En temps réel
* La fenêtre modale ne se fermera plus lorsque tu décide de unliker la musique dans la vue de la
  playlist Favoris
* Quelques autres optimisations

### 2.0.0-beta-1

Améliorations, optimisations et dépannage:

* Définition d'un ordre pour les médias si leur titre est exactement le même lors de la comparaison,
  à l'écran si cela se produit, tu verras les médias dans cet ordre:
    * Musique
    * Album
    * Artiste
    * Genre
    * Playlist
    * Dossier
* Limitation de la liste d'éléments à 300 maximum dans Android Auto en raison de ses limitations
* Le chargement devrait être plus rapide dans Android Auto car une boucle a été supprimée pour
  chaque chargement de liste
* Les boutons des réglages aléatoire ne sont plus des commutateurs mais agissent comme les boutons
  pour le mode de répétition
* Correction du chargement des musiques présentes sur un stockage externe (comme les cartes SD)

### 2.0.0-alpha-1

⚠ La version Alpha n'est pas recommandée pour une utilisation régulière.

Améliorations, optimisations et dépannage:

* La structure du code a été entièrement remaniée pour être plus simple et meilleure à utiliser.
* Le code a été retravaillé pour être plus rapide
* Satunes prend désormais en charge Android 15 Vanilla Ice Cream (API 35)
* Désormais, l'application chargera même vos musiques dupliquées
* Par défaut, Satunes ne chargera que le dossier principal Musiques et pas la totalité.
* Satunes enregistre certaines erreurs via des logs (sans informations personnelles), tu pouvez les
  exporter, rien ne quittera l'application sans votre autorisation.
* Implémentation de l'utilisation de ViewModel et UiState pour une meilleure gestion des états de
  Satunes
* Désormais, la lecture de musique à partir d'un dossier chargera d'abord ses musiques, puis celles
  de ses sous-dossiers, toujours triées par titre.
* Ajout d'icônes pour identifier les paramètres plus facilement.

## 1.3 (Android 5.1.1 Lollipop et ultérieur)

### 1.3.1

Améliorations, Optimisations et Résolutions de problèmes:

* La liste des musiques d'un dossier n'est plus modifiée lorsque l'utilisateur clique sur une
  musique
* L'icône de recherche a été supprimée de la page des paramètres de recherche

### 1.3.0

Nouvelles Fonctionnalités:

* Possiblité de choisir les filtres de recherches activés par défaut
* Demande de confirmation lors d'une suppression dans les playlists
* Tous les types de media (musiques, artistes, etc.) ont un menu d'option lors d'un appuis
  long

Améliorations, Optimisations et Résolutions de problèmes:

* Ajout d'un bouton pour ouvrir F-Droid lorsqu'une mise à jour est disponible

## 1.2 (Android 5.1.1 Lollipop et ultérieur)

### 1.2.1

Améliorations, Optimisations et Résolutions de problèmes:

* Correction du bug lors de l'ajout de la musique en cours de lecture à une playlist lorsqu'il n'y
  en a aucune et que l'utilisateur annule sa création
* Ajout d'un filtre de recherche pour les playlists
* Les animations de navigations entre les pages est plus rapide et évite des cliques sur la page
  précédente
* La page de la liste des musique en attente s'ouvre avec en tête la musique en cours de lecture
* Ajout d'un bouton permettant d'ouvrir la musique en cours de lecture depuis la page de recherche
* Le filtre musiques dans la recherche est activé par défaut

### 1.2.0

Nouvelles Fonctionnalités:

* Recherche des différents média
* Système de musique favorites
* Visualisation de la liste de lecture et possibilité de:
    * Lire la musique après le morceaux actuel
    * Ajouter à la file d'attente
* Ajout d'un mode tablet pour l'affichage de la musique en cours de lecutre
* Popup après installation d'une mise à jour

Améliorations, Optimisations et Résolutions de problèmes:

* Suppression du bouton Facebook suite à la future disparition de la page facebook de Satunes
* Optimisations
* Un bug faisant crasher l'application suite à la création de la première playlist a été corrigé

### 1.2.0-preview-1

Nouvelles Fonctionnalités:

* Recherche des différents média
* Système de musique favorites
* Visualisation de la liste de lecture et possibilité de:
    * Lire la musique après le morceaux actuel
    * Ajouter à la file d'attente
* Ajout d'un mode tablet pour l'affichage de la musique en cours de lecutre
* Popup après installation d'une mise à jour

Améliorations, Optimisations et Résolutions de problèmes:

* Suppression du bouton Facebook suite à la future disparition de la page facebook de Satunes
* Optimisations

## 1.1 (Android 5.1.1 Lollipop et ultérieur)

### 1.1.1

Améliorations, Optimisations et Résolutions de problèmes:

* La lecture de la musique suivante lorsque il s'agit de la dernière musique (après activation du
  mode aléatoire) ne fait plus crasher l'application
* Le chargement des playlists est plus rapide au démarrage de l'application

### 1.1.0

Améliorations, Optimisations et Résolutions de problèmes:

* La taille de l'icône repeat one dans les paramètre de lecture par défaut s'affiche correctement
  sur les différentes tailles d'écran
* Mise à jour des librairies

### 1.1.0-beta-1

Nouvelles Fonctionnalités:

* Ajout d'un nouveau paramètre permettant d'utiliser l'Audio Offload (envoie de la lecture dans un
  processus dédié). Cette option permet d'optimiser la batterie
* Possiblité d'ajouter la musique en cours de lecture dans des playlists via les actions rapides
* Navigation vers les albums, genres, artistes, etc. depuis le menu d'un appui long sur une
  musique

Améliorations, Optimisations et Résolutions de problèmes:

* Problème d'affichage lors du lancement de la première musique dans Android Auto réglé
* La séléction du mode aléatoire par défaut est représenté par des boutons avec icônes et plus à
  l'aide d'un switch
* Affichage du nom des boutons de la barre de navigation

## 1.0 (Android 5.1.1 Lollipop et ultérieur)

### 1.0.3

Améliorations, Optimisations et Résolutions de problèmes:

* L'album n'est plus affiché dans le playback si la taille de la hauteur de l'écran est trop petite
  en mode paysage
* Résolution du crash lors du clique sur le bouton paramètre lorsque la permission audio n'est pas
  accordée
* La langue par défaut si la langue du système n'est pas prise en charge est maintenant l'anglais et
  plus le français
* Les titres des albums ne dépasseront plus la largeur de l'album
* Corrections de la taille du texte de la section "Musique" dans les vues artiste et genre
* L'ajout d'une musique dans plusieurs playlist est maintenant résolu
* Résolution du problème lors de l'ajout de 2 playlists de même nom avec des majuscules et minuscule
  sur des lettres différentes
* Lors de l'ajout d'une musique à une playlist et qu'aucune playlist n'a été créée, l'application
  lance la création d'une nouvelle playlist

### 1.0.2

Améliorations, Optimisations et Résolutions de problèmes:

* La navigation dans les paramètres fonctionne de manière uniforme pour tous les menus.
* Amélioration du design

### 1.0.1

Améliorations, Optimisations et Résolutions de problèmes:

* Les icones dans la vue de la musique en cours de lecture n'est plus totalement blanc ni totalement
  noir lorsque désactivé.
* Harmonisation des icones dans les paramètre en les rendant rond.
* Ajout d'une marge horizontale dans la vue du paramètre Android Auto
* Problème résolu pour le téléchargement d'une mise à jour stable

### 1.0.0

Satunes est maintenant stable!

Améliorations, Optimisations et Résolutions de problèmes:

* Le problème lié à la navigation dans les genres ou albums avec un nom contenant le caractère '/'
  peut maintenant fonctionner normalement
* Le design a été amélioré:
    * Les boutons shuffle et repeat dans la vue playback (uniquement dans l'application sur
      téléphone)
      sont complètement rond
    * La prise en charge des tailles d'écran à été améliorée
* Ajout d'un menu dans les paramètres expliquant comment activer Android Auto

### 1.0.0-preview-2

Nouvelles Fonctionnalités:

* Possibilité de changer le nom d'une playlist

Améliorations, Optimisations et Résolutions de problèmes:

* Les problèmes lié au playback dans Android Auto sont réglés:
    * Pas d'affichage au lancement
    * Barre de progression pendant le mode répéter un seul morceau qui va au delà de la limite de
      temps
* Ajout d'un texte expliquant pourquoi l'exportation et exportation des playlists est en bêta
* Tentative de correction du crash lors de l'ouverture de l'app après un long moment après une pause

### 1.0.0-preview-1

Cette version est prévue d'être déployée comme version stable si aucun bug n'est détecté pendant
cette phase.

Nouvelles Fonctionnalités:

* Ajout d'un bouton shuffle dans toutes les vues Android Auto
* Suppression de l'onglet "Musics" car trop d'éléments dans Android Auto.
* Nouveaux paramètres:
    * Choix du mode répétition
    * Choix du mode shuffle
    * Possibilité d'empêcher les autres application de mettre le playback en pause/play

Améliorations, Optimisations et Résolutions de problèmes:

* Les accents sont correctement triés
* La barre de progression ne bug plus lors de l'activation/désactivation du mode shuffle
* La lecture en arrière plan fonctionne correctement en fonction du paramètre choisi
* Les mises à jour prendront en compte les alpha, beta et preview 1,2,3, etc. par exemple "
  1.0.0-preview-1"
* Le multilangage est maintenant prit en charge dans Android Auto
* Le bouton shuffle dans Android Auto a un icone
* Modification du lien pour github
* Amélioration du design et prise en charge de différentes taille d'écran.

## 0.10 (Android 5.1.1 Lollipop et ultérieur)

### 0.10.1-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Les morceaux sont triés par ordre alphabétique dans les dossiers, cela évite le mauvais ordre de
  lecture.
* Optimization des performances et de la batterie lors de la lecture. Le rafraichissement de la
  barre de progression se fait uniquement lorsque la vue concernée est ouverte.

### 0.10.0-beta

L'application est maintenant disponible pour les appareils Android Lollipop (5.1.1) et plus récents.
Environ 99.2% d'entre vous peuvent l'installer 😜.

Nouvelles Fonctionnalités:

* Il est possible de changer la vitesse de rafraichissement de la barre de progression
* Il est possible d'exporter et d'importer les playlists, au format json (des améliorations future
  seront mises au point.)
* Il est possible d'exclure les sonneries

Améliorations, Optimisations et Résolutions de problèmes:

* La façon dont les permissions sont gérée aa été amélioré et l'application ne plante plus lors du
  premier démarrage
* Le design a été amélioré:
    * Afin d'éviter les blocage dans les petits écran, toutes les éléments sont scrollable en
      attente
      de trouver une solution plus adéquate
    * L'image de l'album est affichée à l'écran dans la page de l'album
    * Les différentes vues sont dans leur entierté déroulable
    * La vue des genres affiches, en plus des musiques, les albums comme dans la page d'un artiste
    * Le nom de l'artiste est affiché dans la vue de l'album et permet une redirection vers celui-ci
    * Les zones de textes ont été harmonisées et les long textes seront coupés si nécéssaire
* Quelques optimisations légères ont été apportées

## 0.9 (Android 9 Pie et ultérieur)

### 0.9.2-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Amélioration du tri
* Les albums contiennent plus qu'une musique
* Prise en compte du cas où aucune donnée n'a été chargée.

### 0.9.1-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Changement du nom de domaine vers "io.github.antoinepirlot.satunes".
* Le design a été retouché pour apporter des pochette d'album par défaut partout dans l'application.
* Les pochettes d'albums s'affichent dans Android Auto.
* Les migration de la base de données pourront être faites.
* Réglage de quelques problème lors de l'initialisation de Android Auto dans certains cas.
* La recomposition de l'écran lors de l'ajout de données comme des playlist est maintenant
  opérationnel.

### 0.9.0-beta

MP3 Player porte enfin son propre nom "Satunes". Cette version devient compatible Android 9 Pie et
plus récent.

Nouvelles Fonctionnalités:

* Un click sur la notification ouvre l'application là où l'utilisateur l'a laissé.
* Compatibilité avec Android 9 Pie et plus récent
* Possibilité d'ajouter plusieurs musiques dans une playlist
* Possibilité de lancer les modes aléatoire et répéter un morceau dans Android Auto

Améliorations, Optimisations et Résolutions de problèmes:

* Le design a été amélioré.
* Les paramètres ont été déplacés dans des sous sections pour une meilleure séparations.
* Les mises à jours pourront se faire peu importe le nom de l'application
* Les toasts ne seront plus affichés 2 fois lors de la mise à jour.
* L'image des albums s'affiche dans les listes des albums et musiques afin de rendre les listes plus
  sympatique.
* Optimisation de l'utilisation de la batterie
* Le lancement d'une musique depuis les playlists est maintenant réglé.
* La librairie qui permet la navigation a été mise à jour et apporte de nouvelles animations.

## 0.8 (Android 11 Red Velvet Cake et ultérieur)

### 0.8.3-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Tous les albums qui ont le même noms seront affiché à l'écran.
* Les artistes, albums et genres sans noms auront un nom associé.

### 0.8.2-beta

Améliorations, Optimisations et Résolutions de problèmes:

* Les albums contiennent toutes leurs musiques. Un album est considéré comme identique si son titre
  et son artist sont les mêmes.
* Maintenant il est possible de télécharger et d'installer la mise à jour depuis l'application.

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
    * Preview: Il s'agit d'une version candidate qui, si tout ce passe bien, sera déployée en
      version
      stable.
    * Stable: Il s'agit d'une version prête à l'emploi. C'est la version recomandée pour une
      utilisation normale. Elle peut cependant encore présenter quelques bugs mais ceux-ci se feront
      rare et n'empêcheront pas le bon fonctionnement des fonctionalités.
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
* Possibilité de cliquer sur l'album (dans la page d'un artiste ou depuis la musique en cours de
  lecture) pour afficher la page de l'album.

Améliorations, Optimisations et Résolutions de problèmes:

* Le paramètre qui permet de masquer l'onglet fichier est réparé, le paramètre est enregistré
  correctement.
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
    * Pour activer Android Auto clique [ici](README.md) pour savoir comment faire.

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
    * Authorise l'utilisateur à choisir d'arrêter la lecture si l'application est fermée depuis le
      multi-âche.
    * Authorise l'utilisateur à choisir d'arrêter la lecture si le casque/écouteurs sont
      déconnectés.

Résolutions de problèmes:

* La musique provenant d'un stockage externe peut être lue.
* Affiche les différents stockage ("Cet appareil", "Carte SD", etc.)

## 0.3 (Android 14 Upside Down Cake et ultérieur)

### 0.3.2-beta

Résolutions de problèmes:

* Désactive la mise en aléatoire depuis l'extérieur de l'application.

### 0.3.1-beta

Résolutions de problèmes:

* Le chargement des données sur les appareils Galaxy ne font plus crasher l'application si aucune
  image d'album n'est trouvée.
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

Dans cette version, l'image de l'album est montré dans la vue lecture en cours et la bar de
progression à été déplacée en bas de l'écran.

## 0.1 (Android 14 Upside Down Cake et ultérieur)

### 0.1.0-beta

Cette version est la toute première version de l'application (en bêta).
Cela inclus les fonctionnalités de base d'un lecteur MP3.
