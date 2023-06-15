import networkx as nx
import pandas as pd

g = nx.read_edgelist("../original_data/loc-gowalla_edges.txt.gz", delimiter="\t", nodetype=int)

region = "usa"

users_from_x = pd.read_csv("{0}/user_locations.csv".format(region))

print(users_from_x.shape)

users = [int(i) for i in users_from_x['user_id']]

g1 = g.subgraph(users)

Gcc = max(nx.connected_components(g1), key=len)
g2 = g1.subgraph(Gcc)

print(len(g2.nodes()))
print(len(g2.edges()))
print(nx.is_connected(g2))

users = [int(i) for i in g2.nodes()]

df = users_from_x
df = df[df["user_id"].isin(users)]
df.to_csv(region + "/user_locations2.csv", index=False)
print(df.shape)

nx.write_edgelist(g2, region + "/edge_list.csv", data=False, delimiter=",")

# print(df[df["user_id"].isin(users)])
# print(users)
