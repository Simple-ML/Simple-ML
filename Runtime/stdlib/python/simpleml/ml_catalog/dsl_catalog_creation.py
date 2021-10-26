import os
import re
import csv
from rdflib import Graph, URIRef, Literal
from rdflib.namespace import Namespace, RDF, OWL, SKOS, RDFS, DCTERMS
from rdflib.namespace import DefinedNamespace, Namespace


# StubClass
#	stub_id
#	name
#	is_open
#	has_process

# Process:
#	has_input: Parameter
#	has_output: StubClass

# Parameter
#	name: String
#	class StubClass
#	is_optional: Boolean
#	default: Literal

class DSL_O(DefinedNamespace):
    _fail = True

    # http://www.w3.org/1999/02/22-rdf-syntax-ns#Property
    name: URIRef
    is_open: URIRef
    is_interface: URIRef
    is_optional: URIRef
    default_value: URIRef
    has_function: URIRef
    has_parameter: URIRef
    has_input_parameter: URIRef
    has_output_parameter: URIRef
    has_type: URIRef
    id: URIRef
    abbreviation: URIRef

    # http://www.w3.org/2000/01/rdf-schema#Class
    Parameter: URIRef
    Stub: URIRef
    Function: URIRef

    _NS = Namespace("http://simple-ml.de/dsl#")

WD = Namespace("http://www.wikidata.org/entity/")
SML = Namespace("http://simple-ml.de/rdf#")

class Parameter:
    def __init__(self, name, type_id, is_optional, default_value=None):
        self.name = name
        self.type_id = type_id
        self.is_optional = is_optional
        self.default_value = default_value
        self.type = None


class Stub:
    def __init__(self, stub_id, name, parent_stub_id=None, parameters_string=None, is_open=None, is_interface=None,
                 imports=list(), package=None):
        self.stub_id = stub_id
        self.name = name
        self.parent_stub_id = parent_stub_id
        self.is_open = is_open
        self.parameters_string = parameters_string
        self.parameters = set()
        self.functions = set()
        self.is_interface = is_interface
        self.imports = imports
        self.package = package
        self.parent = None
        self.names = dict()
        self.descriptions = dict()
        self.abbreviations = dict()


class Function:
    def __init__(self, name, package=None, generics=None, input_parameters_string=None,
                 output_parameters_string=None, imports=list()):
        self.name = name
        self.package = package
        self.input_parameters = set()
        self.output_parameters = set()
        self.input_parameters_string = input_parameters_string
        self.output_parameters_string = output_parameters_string
        self.imports = imports


stub_folder = "../../../../DSL/de.unibonn.simpleml/stdlib/stubs/"

stub_class_regex = "(open )?(class|interface) ([A-Za-z]+)( constructor\(([A-Za-z0-9. :,]*)\))?( sub ([A-Za-z]+))?[ {]*"
attr_regex = "attr ([A-Za-z]+): ([A-Za-z]+)(\??)"
fun_regex = "(override )?fun ([A-Za-z]+)(<([A-Za-z])*>)?\((.*)\)( -> (.+))?"


def parse_stub_file(stub_file, stubs, outside_functions, all_functions, stubs_per_package):
    stub = None
    imports = list()

    for line in stub_file.readlines():
        line = line.strip()
        line = re.sub(' +', ' ', line)  # remove repeated whitespaces
        line = line.replace("<T>", "")  # ignore generics

        if line.startswith("package"):
            package = line.split(" ")[1:][0]

        if line.startswith("import"):
            imports.append(line.split(" ")[1:][0])

        elif line.startswith("class") or line.startswith("open class") or line.startswith("interface"):
            m = re.search(stub_class_regex, line)
            name = m.group(3)
            stub_id = package + "." + name
            stub = Stub(stub_id=package + "." + name, name=name, parent_stub_id=m.group(7),
                        parameters_string=m.group(5), is_open=m.group(1) == "open ",
                        is_interface=m.group(2) == "interface", imports=imports, package=package)
            stubs[stub_id] = stub
            if package not in stubs_per_package:
                stubs_per_package[stub.package] = set()
            stubs_per_package[stub.package].add(stub)

        elif stub and line.startswith("attr"):
            m_attr = re.search(attr_regex, line)
            # stub.parameters.add(Parameter(name = m_attr.group(1), type=m_attr.group(2), is_optional=m_attr.group(3) == "?"))
        elif line.startswith("override fun") or line.startswith("fun"):
            m_fun = re.search(fun_regex, line)
            function = Function(name=m_fun.group(2), generics=m_fun.group(4), input_parameters_string=m_fun.group(5),
                                output_parameters_string=m_fun.group(7))
            if stub:
                stub.functions.add(function)
            else:
                function.package = package
                function.imports = imports
                outside_functions.add(function)

            all_functions.add(function)
        elif line == "}":
            stub = None


def parse_parameterstring(parameters_string):
    parameters = list()
    for parameter_string in parameters_string.split(","):
        parameter_name = parameter_string.split(":")[0].strip()
        parameter_type = parameter_string.split(":")[1].strip()
        default_value = None
        if " or " in parameter_type:
            default_value = parameter_type.split(" or ")[1]
            parameter_type = parameter_type.split(" ")[0].strip()
        is_optional = False
        if parameter_type.endswith("?"):
            is_optional = True
            parameter_type = parameter_type[:-1]
        parameters.append(
            Parameter(name=parameter_name, type_id=parameter_type, is_optional=is_optional,
                      default_value=default_value))
    return parameters


functions_outside = set()
all_functions = set()
stubs = dict()
stubs_per_package = dict()

for subdir, dirs, files in os.walk(stub_folder):
    for file in files:
        # if "model" not in file:
        #    continue
        with open(os.path.join(subdir, file), 'r') as stub_file:
            parse_stub_file(stub_file, stubs, functions_outside, all_functions, stubs_per_package)

for stub in stubs.values():
    if stub.parameters_string:
        for parameter_string in stub.parameters_string.split(","):
            parameter_name = parameter_string.split(":")[0].strip()
            parameter_type = parameter_string.split(":")[1].strip()
            default_value = None
            if " or " in parameter_type:
                default_value = parameter_type.split(" or ")[1]
                parameter_type = parameter_type.split(" ")[0].strip()
            is_optional = False
            if parameter_type.endswith("?"):
                is_optional = True
                parameter_type = parameter_type[:-1]
            stub.parameters.add(Parameter(name=parameter_name, type_id=parameter_type, is_optional=is_optional,
                                          default_value=default_value))

for function in all_functions:
    function.input_parameters.update(parse_parameterstring(function.input_parameters_string))
    if function.output_parameters_string:
        function.output_parameters.update(parse_parameterstring(function.output_parameters_string))

# dissolve dependencies for class/type references
for stub in stubs.values():
    potential_imports = dict()
    stub.imports.append(stub.package + ".*")
    stub.imports.append("simpleml.lang.*")
    for import_stm in stub.imports:
        import_prefix = import_stm.rsplit('.', 1)[0]
        import_suffix = import_stm.rsplit('.', 1)[1]

        if import_suffix == "*":
            for potential_import in stubs_per_package[import_prefix]:
                potential_imports[potential_import.name] = potential_import
        else:
            potential_imports[import_suffix] = stubs[import_stm]

    if stub.parent_stub_id:
        stub.parent = potential_imports[stub.parent_stub_id]

    for parameter in stub.parameters:
        parameter.type = potential_imports[parameter.type_id]

    for function in stub.functions:
        for parameter in function.input_parameters:
            parameter.type = potential_imports[parameter.type_id]
        for parameter in function.output_parameters:
            parameter.type = potential_imports[parameter.type_id]

# TODO: Outside functions

g = Graph()
g.bind('dsl-o', DSL_O)
DSL = Namespace("http://simple-ml.de/dsl#")
g.bind('dsl', DSL)
g.bind('dcterms', DCTERMS)


def make_uri(value):
    return (value[0].upper() + value[1:]).replace(" ", "").replace("(","_").replace(")","_").replace("'","")


def createParameterTriples(g, parameter, parameter_ref):
    g.add((parameter_ref, RDF.type, DSL_O.Parameter))
    g.add((parameter_ref, DSL_O.name, Literal(parameter.name)))
    g.add((parameter_ref, DSL_O.is_optional, Literal(parameter.is_optional)))

    if parameter.type:
        g.add((parameter_ref, DSL_O.has_type, DSL["Stub" + parameter.type.name]))

    if parameter.default_value:
        g.add((parameter_ref, DSL_O.default_value, Literal(parameter.default_value)))

ml_graph = Graph()
ml_graph.parse("../../../data_catalog/ml_processes_catalog/ml_catalog.ttl")

# read additional information and mapping to ML catalog
info_dict = {}
with open("../../../data_catalog/ml_processes_catalog/processes_info.tsv") as infile:
    reader = csv.reader(infile, delimiter="\t")
    headers = next(reader)[1:]
    for row in reader:
        stub = stubs[row[0]]
        for key, value in zip(headers, row[1:]):
            if key.startswith("description"):
                lang = key.split("_")[1]
                stub.descriptions[lang] = value
            elif key.startswith("abbreviation"):
                lang = key.split("_")[1]
                stub.abbreviations[lang] = value
            elif key.startswith("name"):
                lang = key.split("_")[1]
                stub.names[lang] = value

        # Get information from ML catalog via Wikidata ID
        wikidata_id = dict(zip(headers, row[1:]))["wikidata_id"]
        if wikidata_id:
            for algorithm_ref, _, _ in ml_graph.triples((None, OWL.sameAs, WD[wikidata_id])):
                for _, _, pref_label in ml_graph.triples((algorithm_ref, SKOS.prefLabel, None)):
                    if pref_label.language not in stub.names:
                        stub.names[pref_label.language] = pref_label
                for _, _, pref_abbreviation in ml_graph.triples((algorithm_ref, SML.prefAbbreviation, None)):
                    if pref_abbreviation.language not in stub.pref_abbreviations:
                        stub.abbreviations[pref_abbreviation.language] = pref_abbreviation
                for _, _, pref_description in ml_graph.triples((algorithm_ref, SML.prefDescription, None)):
                    if pref_description.language not in stub.descriptions:
                        stub.descriptions[pref_description.language] = pref_description

for stub in stubs.values():

    stub_ref = DSL["Stub" + stub.name]
    g.add((stub_ref, RDF.type, DSL_O.Stub))
    g.add((stub_ref, DSL_O.name, Literal(stub.name)))
    g.add((stub_ref, DSL_O.id, Literal(stub.stub_id)))

    for lang, description in stub.descriptions.items():
        g.add((stub_ref, DCTERMS.description, Literal(description, lang)))
    for lang, name in stub.names.items():
        g.add((stub_ref, RDFS.label, Literal(name, lang)))
    for lang, abbreviation in stub.abbreviations.items():
        g.add((stub_ref, DSL_O.abbreviation, Literal(abbreviation, lang)))

    if stub.parent:
        g.add((stub_ref, RDFS.subClassOf, DSL["Stub" + stub.parent.name]))

    # print(stub.stub_id)
    for function in stub.functions:
        function_ref = DSL["Function" + stub.name + make_uri(function.name)]
        g.add((function_ref, RDF.type, DSL_O.Function))
        g.add((function_ref, DSL_O.name, Literal(function.name)))
        g.add((stub_ref, DSL_O.has_function, function_ref))
        for parameter in function.input_parameters:
            parameter_ref = DSL[
                "ParameterInput" + make_uri(stub.name) + "Function" + make_uri(function.name) + make_uri(
                    parameter.name)]
            g.add((function_ref, DSL_O.has_input_parameter, parameter_ref))
            createParameterTriples(g, parameter, parameter_ref)
        for parameter in function.output_parameters:
            parameter_ref = DSL[
                "ParameterOutput" + make_uri(stub.name) + "Function" + make_uri(function.name) + make_uri(
                    parameter.name)]
            g.add((function_ref, DSL_O.has_output_parameter, parameter_ref))
            createParameterTriples(g, parameter, parameter_ref)

    for parameter in stub.parameters:
        parameter_ref = DSL["Parameter" + stub.name + make_uri(parameter.name)]
        g.add((stub_ref, DSL_O.has_parameter, parameter_ref))
        createParameterTriples(g, parameter, parameter_ref)

for function in functions_outside:
    function_ref = DSL["Function" + make_uri(function.name)]
    g.add((function_ref, RDF.type, DSL_O.Function))
    g.add((function_ref, DSL_O.name, Literal(function.name)))
    for parameter in function.input_parameters:
        parameter_ref = DSL["ParameterInput" + "Function" + make_uri(function.name) + make_uri(parameter.name)]
        g.add((function_ref, DSL_O.has_input_parameter, parameter_ref))
        createParameterTriples(g, parameter, parameter_ref)
    for parameter in function.output_parameters:
        parameter_ref = DSL["ParameterOutput" + "Function" + make_uri(function.name) + make_uri(parameter.name)]
        g.add((function_ref, DSL_O.has_output_parameter, parameter_ref))
        createParameterTriples(g, parameter, parameter_ref)




g.serialize(destination="../../../data_catalog/ml_processes_catalog/process_catalog.ttl")

for s, p, o in g.triples((None, None, DSL_O.Stub)):
    for s, p2, o2 in g.triples((s, DSL_O.has_function, None)):
        print(s, " -> ", o2)

#TODO: splitIntoTrainAndTest misses outputs