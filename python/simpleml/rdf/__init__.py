import simpleml.util.global_configurations as global_config

if global_config.local_rdf:
    from ._sparql_connector_local import load_query, run_query
else:
    from ._sparql_connector import load_query, run_query
