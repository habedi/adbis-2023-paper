import random as rand

import networkx as nx
import pandas as pd

region = "usa"

size = 10000

# loc = "mini"
loc = str(size)

g = nx.read_edgelist(region + "/edge_list.csv", delimiter=",")

print(len(g.nodes()))
print(len(g.edges()))

user_locations = pd.read_csv(region + "/user_locations.csv", sep=',', header=None, index_col=False,
                             names=["user_id", "latitude", "longitude"])

print(user_locations.shape)

nodes = [n for n in g.nodes()]


def f(nodes=nodes):
    seed = rand.sample(nodes, 1)[0]
    subset_nodes = set()
    for i in nx.bfs_edges(g, seed):
        subset_nodes.add(i[0])
        subset_nodes.add(i[1])
        if len(subset_nodes) >= size:
            break
    return subset_nodes


mini_g = nx.subgraph(g, f(nodes=nodes))

while not nx.is_connected(mini_g) or nx.density(mini_g) < 0.001:
    mini_g = nx.subgraph(g, f(nodes=nodes))

print(nx.is_connected(mini_g))
print(nx.density(mini_g))
print(len(nx.edges(mini_g)))
print(len(nx.nodes(mini_g)))

mini_g_nodes = [int(n) for n in nx.nodes(mini_g)]
user_locations2 = user_locations.loc[user_locations['user_id'].isin(mini_g_nodes)]

user_locations2.to_csv(region + "/" + loc + "/user_locations.csv", index=False, header=None)
print(user_locations2.shape)

nx.write_edgelist(mini_g, region + "/" + loc + "/edge_list.csv", data=False, delimiter=",")

for n in mini_g_nodes:
    if n not in user_locations["user_id"].values:
        print(n)
