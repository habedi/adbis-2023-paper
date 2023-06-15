import os

import networkx as nx
from fastnode2vec import Node2Vec, Graph

# original dataset name
# dataset = "usa"
# dataset = "usa/mini"
dataset = "usa/3000"

EMBEDDING_FILENAME = "{0}/vertex_embeddings.txt".format(dataset)

# Load the graph
g = nx.read_edgelist("{0}/edge_list.csv".format(dataset), nodetype=str, delimiter=",")

graph = Graph(g.edges(), directed=False, weighted=False)

n2v = Node2Vec(graph, dim=16, walk_length=100, context=10, p=2.0, q=0.5, workers=24)

n2v.train(epochs=100)

model = n2v

# Save embeddings for later use
model.wv.save_word2vec_format(EMBEDDING_FILENAME)

EMBEDDING_FILENAME_CSV = EMBEDDING_FILENAME + ".csv"
csv_file = """sed -e 's/\s\+/,/g' {0} > {1}""".format(EMBEDDING_FILENAME, EMBEDDING_FILENAME_CSV)

os.system(csv_file)
os.system("rm {0}".format(EMBEDDING_FILENAME))
os.system("mv {0} {1}".format(EMBEDDING_FILENAME_CSV, EMBEDDING_FILENAME))

print("done!")
