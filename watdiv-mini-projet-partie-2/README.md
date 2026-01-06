# Mini-Projet RDF Partie 2 : Évaluation et Analyse des Performances


##  Politique d'utilisation des IA
Ce module propose un parcours pédagogique qui est conçu excluant l'utilisation des IA.
S'appuyer sur les LLM pour le développement du code revient à abandonner ce projet pédagogique ;
dans ce cas, aucune garantie sur la pertinence du parcours pédagogique du module ne peut être assurée.

L'utilisation des IA est permise pour ce qui concerne l'automatisation de tâches répétitives ou de mise en forme liées au projet ou rapport, ou encore pour aider à mieux comprendre le problème étudié.
L'idée est que tout informaticien "moderne" soit capable d'utiliser ces outils pour extraire le plein potentiel de son travail ; en même temps l'objectif de ce projet et du module est d'accroître vos connaissance, et cela passe nécessairement par un travail personnel ainsi que par les interactions du travail de groupe.
La seule condition imposée dans ce cours est que l’utilisation des IA soit déclarée dans tous vos rendus.
Par exemple, si le rapport a été mis en forme avec un assistant d'IA, il faut indiquer "utilisation IA pour création / mise en forme du rapport".
Important : utiliser des IA n’a pas vocation à entraîner de pénalisations, au contraire en indiquer l'utilisation permet aux enseignants de mieux évaluer votre travail et à récompenser l'effort et l'originalité des différents projets.

Le non-respect de la politique d’utilisation des IA déclarée peut avoir un impact très important sur la notation du travail réalisé.
En revanche, l'apport personnel aux développements réalisés pendant le projet sera fort apprécié.


## Objectif

L'objectif de cette deuxième partie du projet est d'évaluer et analyser les performances de votre prototype.  
Précisément, les performances de votre système doivent être comparées en utilisant le benchmark WatDiv avec :

1. La dernière version de InteGraal.
2. Une implémentation réalisée par vos collègues (à vous de choisir laquelle).  

## Travail à rendre

Voir [ici](../README.md)

## Travail demandé

Cette partie s'articule en 3 blocs:

1. Préparation des bancs d'essais : l'objectif est d'obtenir un jeu de données et de requêtes que vous maîtrisez 
2. Planification des tests à réaliser : l'objectif est de produire un document indiquant les tests que vous voulez réaliser ; cette partie est objet d'un rendu intermédiaire (voir section précédente).
3. Exécution des tests : l'objectif est d'implémenter vos protocoles de test, de réaliser les expériences.

Notez que les blocs 1 et 2 peuvent être réalisés en parallèle : vous n'avez pas besoin de terminer la préparation des bancs d'essai pour planifier les tests à réaliser.

Une fois ce travail réalisé, la dernière phase est celle restitution : vous devrez analyser vos résultats et restituer cela avec des graphiques.

---

## Document final

**Les réponses aux questions suivantes doivent être incluses dans le rapport final du projet, que vous pouvez organiser librement. La seule contrainte concerne la taille du rapport qui ne doit pas dépasser les 10 pages (annexes exclus).**


---

## Benchmarks

1. Quels **types** de benchmark avez-vous utilisés pour vos tests pendant le développement ?  
2. Décrivez un test possible pour chaque **type** de benchmark (micro, standard, réel).
3. Vous allez utiliser le benchmark [WatDiv](http://dsg.uwaterloo.ca/watdiv/) pour vos tests.  
   Présentez brièvement le benchmark, qu'est WatDiv ?

---

## Préparation des bancs d'essais

Attention   : 
- ⚠️ Le fonctionnement de Watdiv n'est garanti que dans les ordinateurs de la faculté.
- ⚠️ Utilisez la version de Watdiv preconfigurée qui est mise à disposition dans ce dépôt.


1. Utiliser la version de WatDiv mise à disposition pour générer des bases de données de tailles différentes, ainsi que des jeux de requêtes pour vos tests.  
   - **Pour générer des données** : Compilez WatDiv (C++) selon les instructions ci-dessous ou sur le [site de WatDiv](https://dsg.uwaterloo.ca/watdiv/#installation). Vérifiez que la bibliothèque **BOOST** est installée.
   - **Format des données** : WatDiv génère des fichiers au format N3. Si nécessaire, utilisez [rdf2rdf](http://www.l3s.de/~minack/rdf2rdf/) pour les convertir en RDF/XML.
   - **Pour générer des requêtes** : Modifiez le script `regenerate_queryset.sh` pour ajuster le nombre de requêtes par template (par défaut : 100).  
     Des patrons de requêtes sont disponibles dans :
     - Le répertoire `testsuite` pour les requêtes **en étoile**.
     - Le répertoire `more query templates` pour des requêtes générales. Vous pouvez consulter leur description [ici](https://dsg.uwaterloo.ca/watdiv/basic-testing.shtml).

2. Créer un jeu de tests pour :
   - Les requêtes **en étoile**.

   ⚠️ La validité des expériences dépend de ce passage clé. Vous devrez générer plusieurs requêtes avec WatDiv.

3. Une fois votre jeu de requêtes de test créé, représentez les résultats avec un histogramme montrant :
   - Le nombre de réponses aux requêtes sur une instance de **500K** et de **2M** triples.
4. Combien de requêtes ont zéro réponses ?  
5. Combien de requêtes sont en étoile ?

---

## Hardware et Software

1. Quel type de **hardware** et de **software** avez-vous utilisé pour vos tests précédents ?  
2. Est-ce adapté à l’analyse des performances du système ? Justifiez.  

---

## Métriques, Facteurs, et Niveaux

1. Donnez une liste de **métriques** permettant d’évaluer les moteurs de requêtes RDF.  
2. Listez les **facteurs** qui interviennent dans l'évaluation du système, et définissez-en les niveaux.  
3. Ordonnez les facteurs par importance et identifiez les facteurs principaux et secondaires.

---

## Évaluation des performances

1. Pour quelles métriques est-il préférable d’effectuer des mesures **“cold”** ou **“warm”** (ou les deux) ?  
2. Comment allez-vous réaliser ces mesures en pratique ?  
3. Proposez une procédure pour vérifier la correction et la complétude de votre système.  
   - Tester les systèmes sur des requêtes en étoile et, si votre système le permet, sur des requêtes générales.
4. Réalisez une expérience **2²** en faisant varier la taille des données et la mémoire. Que pouvez-vous conclure ?  
5. Planifiez une expérience **2²** (ou plusieurs) pour analyser l’impact des optimisations. Les optimisations sont-elles effectives ? Expliquez vos résultats.  

   **Pour chaque test 2²** :  
   - Choisissez deux facteurs et deux niveaux.  
   - Calculez l’importance des facteurs via un modèle de régression et interprétez les résultats.

6. Vérifiez si l’ordre d’évaluation des requêtes dans le workload joue un rôle dans l’analyse des performances.
7. Comparez votre système avec **InteGraal** et avec une implémentation concurrente sur des requêtes en étoile.  
   - Quelles conclusions pouvez-vous en tirer ? Expliquez les résultats.  
8. Dans le système concurrent choisi :
   - Les mesures sont-elles réalisées de manière comparable (vérifiez le code source) ?
   - Le système est-il correct et complet ?
9. Toute expérience permettant de mieux comprendre votre système est bienvenue.

---

## Représentation graphique des résultats

1. Présentez les résultats de vos tests dans des histogrammes et analysez-les.  
2. Expliquez les différences de performances entre votre prototype et les systèmes concurrents.

---

## Compilation de WatDiv

### Instructions pour Linux

Utilisez les comptes de la faculté via **x2go**.

**Pour compiler :**  
```bash
make clean
make
```

**Pour générer les données** (le facteur d’échelle 1 génère 15MB de données, 10 -> 150MB, etc.) :

```bash
bin/Release/watdiv -d model/wsdbm-data-model.txt 1
```

Pour sauvegarder les données dans un fichier.
```bash
bin/Release/watdiv -d model/wsdbm-data-model.txt 1 > data.nt
```

**Pour générer des requêtes** :

Ovrez les patrons de requêtes dans le répertoire `testsuite/templates` pour visualiser le fonctionnement du générateur de requêtes.
Modifiez puis exécutez le script `regenerate_queryset.sh` (par exemple, modifiez le nombre de requêtes générées par patron, actuellement fixé à 100).

```bash
. ./regenerate_queryset.sh
```

En cas de problèmes non résolus avec WatDiv, vous pouvez utiliser des données et requêtes mises à disposition [au lien suivant](https://gitlab.etu.umontpellier.fr/p00000013857/watdiv-backup). Attention, il s'agit d'un dépôt de "dépannage" contenant données et requêtes "brutes" qui doivent être traitées avant de pouvoir être utilisées pour l'analyse des performances.

# Tests avec BRunner

BRunner ([article](https://hal-lirmm.ccsd.cnrs.fr/lirmm-04646842v2/file/brunner_demo_BDA24.pdf),[git](https://gitlab.inria.fr/rules/brunner/-/tree/rdfstorage2/)) est un outil permettant d'automatiser l'exécution de tests de performance pour des logiciels Java

Votre moteur de requête utilise un API bien définie par l'interface `RDFStorage`. 
Vous avez aussi deux implémentations de cette interface `RDFHexaStore` et `GiantTable` : 
on va pouvoir s'en servir pour automatiser les tests sur vos implémentations. 

**Veillez à que vos implémentations utilisent bien ces noms (autrement l'automatisation des tests va échouer)!**

Voici les étapes pour tester votre moteur avec BRunner.


Mettre à jour (git pull) le projet (des modifications mineures sur le pom ont été faites)

```
# installer votre projet sur votre machine
git pull
mvn clean install
```

récupérer le chemin absolu vers le jar (avec dépendances) qui se trouve dans le repertoire target : vous allez utiliser ce chemin pour lancer le test
```
ls target
```

Télécharger Brunner
```
# 1 cloner branche sans checkout
git clone --no-checkout --branch rdfstorage2 https://gitlab.inria.fr/rules/brunner.git
cd brunner

# 2  mode sparse-checkout
git sparse-checkout init --cone

# 3 dossiers à garder
git sparse-checkout set \
brunner-RDFStorage \
brunner \
scripts

# extraire branche spécifiée
git checkout rdfstorage2
```

Installer BRunner-RDFStorage
```
# installer
. ./install.sh
```

Tester le système de benchmarking
```
# accéder au répértoire brunner-RDFStorage
cd brunner-RDFStorage

# tester un jar de test (quel système peut bien se cacher derrière le jar?)
. ./benchMyRDFEngine.sh testEngine.jar

#tester votre application
. ./benchMyRDFEngine.sh /chemin/vers/le/jar/de/mon/moteur/de/requetes/rdf

# récupérer un fichier de configuration brunner et le passer en paramètre : vérifier l'existance du fichier dans
ls target/classes/demo-files/config-files/BenchRDFStorage.brn

#tester avec un fichier de configuration  
. ./benchMyRDFEngine.sh testEngine.jar  target/classes/demo-files/config-files/BenchRDFStorage.brn

```

Travail demandé : 

1. Tester votre logiciel avec BRunner.
2. Explorer les protocoles expérimentaux implémentés dans `src/main/java/rdf_storage/services`.
3. Modifier le fichier de configuration `BenchRDFStorage.brn` pour ajouter un test LOAD_GIANT_TABLE qui teste le protocole de chargement des données pour l'implémentation de la Giant-Table
4. Modifier le fichier de configuration `BenchRDFStorage.brn` pour ajouter un nouveau scénario de test (données + requetes)  
5. (optionnel) Modifier les protocoles pour éviter les écritures dans la sortie standard. Il faudra ensuite recompiler brunner-RDFStorage et utiliser le jar mis à jour avec le nouveau protocole (cela devrait être choisi automatiquement lors du lancement du script `benchMyRDFEngine.sh`).

Pour explorer et exécuter le code depuis votre IDE (notez qu'un vrai test de performance ne se fait pas depuis l'IDE): 
1. Utilisez les options `JVM` suivantes pour désactiver le logging en Java. Il faudra pointer vers les fichiers qui se trouvent dans le repertoire `brunner-RDFStorage/configuration`.
```
-Djava.util.logging.config.file="chemin/vers/configuration/silent-logging.properties" -Dlogback.configurationFile="chemin/vers/configuration/logback-rdfstorage.xml"
```
2. Pour être certains que votre propre projet soit inclus dans le classpath lors de l'exécution depuis l'IDE activez l'option `add dependencies with provided scope to classpath`
3. Pour déboguer ajoutez l'option `-basic` aux paramètres du programme (cela désactive l'utilisation de JMH)

En cas de problèmes n'hésitez pas à utiliser `invalidate caches` et puis rédemarrer IntelliJ.