%====================================================================================
% sonarqak23 description   
%====================================================================================
context(ctxsonarqak23, "localhost",  "TCP", "8020").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8021").
 qactor( sonar23, ctxsonarqak23, "it.unibo.sonar23.Sonar23").
  qactor( appl, ctxsonarqak23, "it.unibo.appl.Appl").
