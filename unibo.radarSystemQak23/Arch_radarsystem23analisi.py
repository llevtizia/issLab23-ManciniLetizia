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
with Diagram('radarsystem23analisiArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxprototipo0', graph_attr=nodeattr):
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
          radar=Custom('radar','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
          controller=Custom('controller','./qakicons/symActorSmall.png')
     sonar >> Edge( xlabel='sonardata', **eventedgeattr, fontcolor='red') >> sys
     sys >> Edge(color='red', style='dashed', xlabel='sonardata', fontcolor='red') >> controller
     controller >> Edge(color='blue', style='solid', xlabel='polar', fontcolor='blue') >> radar
     controller >> Edge(color='blue', style='solid', xlabel='ledCmd', fontcolor='blue') >> led
diag
