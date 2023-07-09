%====================================================================================
% coldstorageservice description   
%====================================================================================
context(ctxcoldstorageservice, "localhost",  "TCP", "9990").
context(ctxbasicrobot, "127.0.01",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( serviceaccessgui, ctxcoldstorageservice, "it.unibo.serviceaccessgui.Serviceaccessgui").
  qactor( transporttrolley, ctxcoldstorageservice, "it.unibo.transporttrolley.Transporttrolley").
