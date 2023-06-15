import matplotlib.pyplot as plt
import networkx as nx

graph_path = "greece/edge_list.csv"

g = nx.read_edgelist(graph_path, delimiter=",")

nx.draw(g)  # networkx draw()

plt.draw()  # pyplot draw()
plt.show()
