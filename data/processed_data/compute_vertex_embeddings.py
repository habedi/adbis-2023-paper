import os

import networkx as nx
from node2vec import Node2Vec

dataset = "synthetic/s1"

EMBEDDING_FILENAME = "{0}/vertex_embeddings.txt".format(dataset)

# Load the graph
# graph = nx.read_edgelist("{0}/edge_list_mock.tsv".format(dataset), nodetype=int)
graph = nx.read_edgelist("{0}/edge_list_mock.tsv".format(dataset), nodetype=int, delimiter="\t")

# Precompute probabilities and generate walks - **ON WINDOWS ONLY WORKS WITH workers=1**
node2vec = Node2Vec(graph, dimensions=16, walk_length=30, num_walks=100, workers=16)

# Embed nodes
model = node2vec.fit(window=10, min_count=1, batch_words=4)

# Save embeddings for later use
model.wv.save_word2vec_format(EMBEDDING_FILENAME)

EMBEDDING_FILENAME_CSV = EMBEDDING_FILENAME + ".csv"
csv_file = """sed -e 's/\s\+/,/g' {0} > {1}""".format(EMBEDDING_FILENAME, EMBEDDING_FILENAME_CSV)

os.system(csv_file)
os.system("rm {0}".format(EMBEDDING_FILENAME))
os.system("mv {0} {1}".format(EMBEDDING_FILENAME_CSV, EMBEDDING_FILENAME))

print("done!")
