<h1 align="center">SnakeInc- Client</h1>
<p align="center">
   <img src="https://i.imgur.com/ggSeuzK.png"/>
   <br>
   <b>SnakeInc - Client</b> <i>ist ein Bot, welcher eigenständig und automatisch das Spiel spe_ed<br>
   spielt und im Rahmen des @informatiCup 2021 durch ein Team aus 4 Entwicklern entwickelt wurde.</i>
   <br>
</p>
<p align="center">
  Weitere Informationen zum <b>informatiCup</b> 2021 befinden sich unter
  <a href="https://github.com/informatiCup/InformatiCup2021"><strong>https://github.com/informatiCup/InformatiCup2021</strong></a>
  <br>
</p>
<p align="center">
  <img src="https://img.shields.io/github/languages/top/SnakeInc/client?style=flat-square" />&nbsp;
  <img src="https://img.shields.io/github/contributors/SnakeInc/client?style=flat-square" />&nbsp;
  <img src="https://img.shields.io/github/issues-raw/SnakeInc/client?style=flat-square" />
</p>
<hr>
## Dokumentation

Der SnakeInc-Client, das Herzstück der Aufgabe wurde als Universitäts-Projekt entwickelt. Daher wurde zusätzlich zum Projekt auch eine wissenschaftliche Ausarbeitung in Form eines LaTex-Dokumentes erstellt.

- [Ausarbeitung][elaboration]
- [Aufgabenstellung][task]

### Informationen rund um das Projekt

Der SnakeInc-Client ist ein Bot, welcher automatisiert das Spiel spe_ed spielt und wurde im Rahmen des informatiCup's 2021 entwickelt wurde.
Das Spiel spe_ed ist vom Spielprinzip dem Spiel Curvefever sehr ähnlich. Hierbei geht es darum mit einer Schlange, welche Felder hinter sich blockiert möglichst lange gegenüber anderen Spielern zu überleben. Unser Bot der SnakeInc-Client kann dabei anhand mehrerer Module dieses Spiel automatisch spielen.

#### Module

Der SnakeInc-Client ist in mehrere Module untergliedert, welche eigenständig Werte auf einer Zelle des Boards setzen können. Die Werte einer Zelle können von 0 bis X gehen. Werte über 1 werden als Risiko gesehen, Werte unterhalb von 1 sind Incentives. Der nutzt die Movement-Calculation um anhand dieser Werte die besten Züge auf dem Board zu tätigen. Ein Risiko-Wert eines einzelnen Moduls sollte im Normalfall nicht größer als 1.2 sein.


### Installation


[elaboration]: https://github.com/SnakeInc/latex-ausarbeitung
[task]: https://github.com/InformatiCup/InformatiCup2021/blob/master/spe_ed.pdf