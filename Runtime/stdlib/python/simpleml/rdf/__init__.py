import simpleml.util.global_configurations as global_config

from ._sparql_connector import endpoint_is_running

if not global_config.local_rdf and endpoint_is_running():
    from ._sparql_connector import load_query, run_query
else:
    from ._sparql_connector_local import get_graph, load_query, run_query
