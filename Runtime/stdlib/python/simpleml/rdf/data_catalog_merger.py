import _sparql_connector_local

graph = _sparql_connector_local.get_graph()

graph.serialize("data_catalog.ttl")