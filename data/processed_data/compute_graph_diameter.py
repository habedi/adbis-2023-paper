import networkx as nx

graph_path = "usa/edge_list.csv"

g = nx.read_edgelist(graph_path, delimiter=",")

print(type(g))

print(nx.is_directed(g))
print(nx.is_connected(g))

print("Wait until I compute the diameter of the graph for you.")
print(nx.algorithms.distance_measures.diameter(g))

# print(os.path.abspath(__file__))
