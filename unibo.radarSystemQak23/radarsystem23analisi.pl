%====================================================================================
% radarsystem23analisi description   
%====================================================================================
context(ctxprototipo0, "localhost",  "TCP", "8088").
 qactor( sonar, ctxprototipo0, "it.unibo.sonar.Sonar").
  qactor( radar, ctxprototipo0, "it.unibo.radar.Radar").
  qactor( led, ctxprototipo0, "it.unibo.led.Led").
  qactor( controller, ctxprototipo0, "it.unibo.controller.Controller").
