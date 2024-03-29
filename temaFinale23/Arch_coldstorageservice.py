from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('coldstorageserviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxcoldstorageservice', graph_attr=nodeattr):
          serviceaccessgui=Custom('serviceaccessgui','./qakicons/symActorSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          coldstorageservice=Custom('coldstorageservice','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     serviceaccessgui >> Edge(color='magenta', style='solid', xlabel='ticket', fontcolor='magenta') >> coldstorageservice
     serviceaccessgui >> Edge(color='magenta', style='solid', xlabel='validateticket', fontcolor='magenta') >> coldstorageservice
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='moverobot', fontcolor='magenta') >> basicrobot
     transporttrolley >> Edge(color='blue', style='solid', xlabel='chargeTaken', fontcolor='blue') >> coldstorageservice
     transporttrolley >> Edge(color='blue', style='solid', xlabel='setdirection', fontcolor='blue') >> basicrobot
     transporttrolley >> Edge(color='blue', style='solid', xlabel='disengage', fontcolor='blue') >> basicrobot
     coldstorageservice >> Edge(color='magenta', style='solid', xlabel='store', fontcolor='magenta') >> coldroom
     coldstorageservice >> Edge(color='magenta', style='solid', xlabel='gomoveToIndoor', fontcolor='magenta') >> transporttrolley
     coldstorageservice >> Edge(color='blue', style='solid', xlabel='chargeTaken', fontcolor='blue') >> serviceaccessgui
diag
