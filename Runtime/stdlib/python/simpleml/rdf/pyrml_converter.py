from pyrml import RMLConverter

rml_converter = RMLConverter()

rml_file_path = os.path.join('examples', 'artists', 'artist-map.ttl')
rdf_graph = rml_converter.convert(rml_file_path)

# Print the triples contained into the RDF graph.
for s, p, o in rdf_graph:
    print(s, p, o)
