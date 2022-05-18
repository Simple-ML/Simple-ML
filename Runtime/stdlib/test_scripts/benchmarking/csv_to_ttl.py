import ast # abstract syntax tree, to str->dict
import pandas as pd
from rdflib import Graph, Literal, RDF, Namespace, DC, XSD


def build_benchmark_graph(dataset_name, generate_small_ttl=False):
    df = pd.read_csv('{0}/{0}_benchmarking.csv'.format(dataset_name))

    g = Graph()
    g.bind("rdf", RDF)
    g.bind("dcterms", DC)
    SML = Namespace("https://simple-ml.de/resource/")
    g.bind("sml", SML)
    MEXPERF = Namespace("http://mex.aksw.org/mex-perf#")
    g.bind("mex-perf", MEXPERF)
    MEXALGO = Namespace("http://mex.aksw.org/mex-algo#")
    g.bind("mex-algo", MEXALGO)

    used_names = []

    for idx, row in df.iterrows():
        # Skip configurations of ML-models, that are not implemented in python.
        if 'ERROR' in row['model']:
            continue

        # If need to create small ttl for easy-readable visualisation and checking
        if generate_small_ttl:
            if row['model'] in used_names:
                continue
            else:
                used_names.append(row['model'])
            if len(used_names) > 3:
                break

        g.add((SML['Benchmark{}{}'.format(idx, row['model'])], RDF.type, SML['Benchmark']))
        g.add((SML['Benchmark{}{}'.format(idx, row['model'])], SML['targetDataSet'], SML['{0}'.format(dataset_name)]))
        g.add((SML['Benchmark{}{}'.format(idx, row['model'])], MEXALGO['hasAlgorithmClass'], MEXALGO[row['model']]))

        parameters = ast.literal_eval(row['parameters'])
        for param_idx, param_info in enumerate(parameters.items()):
            param_name, param_val = param_info[0], param_info[1]
            g.add((SML['Benchmark{}{}'.format(idx, row['model'])],
                   SML['hasParameter'],
                   SML['Benchmark{}{}Parameter{}'.format(idx, row['model'], param_idx)]))

            g.add((SML['Benchmark{}{}Parameter{}'.format(idx, row['model'], param_idx)],
                   RDF.type,
                   SML['Parameter']))

            g.add((SML['Benchmark{}{}Parameter{}'.format(idx, row['model'], param_idx)],
                   SML['ParameterName'],
                   Literal(param_name)))

            g.add((SML['Benchmark{}{}Parameter{}'.format(idx, row['model'], param_idx)],
                   SML['ParameterValue'],
                   Literal(param_val)))

        metrics_names = row.keys()[2:-2]
        benchmark_type = 'Classification' if 'Classifier' in row['model'] else 'Regression'

        for metric_idx, metric_name in enumerate(metrics_names):
            g.add((SML['Benchmark{}{}'.format(idx, row['model'])],
                   SML['perfomance'],
                   SML['Benchmark{}{}Perfomance'.format(idx, row['model'])]))

            g.add((SML['Benchmark{}{}Perfomance'.format(idx, row['model'])],
                   RDF.type,
                   MEXPERF['{}Measure'.format(benchmark_type)]))

            g.add((SML['Benchmark{}{}Perfomance'.format(idx, row['model'])],
                   MEXPERF[metric_name],
                   Literal(row[metric_name], datatype=XSD.double)))

        g.add((SML['Benchmark{}{}'.format(idx, row['model'])],
               SML['dslCode'],
               Literal(row['dsl_code'])))

    g.serialize('{}_graph.ttl'.format(dataset_name), format='turtle')


def build_benchmark_graphs(dataset_names, generate_small_ttl=False):
    for dataset_name in dataset_names:
        try:
            build_benchmark_graph(dataset_name, generate_small_ttl)
        except Exception as error_type:
            print('#' * 10)
            print('building benchmarking KG for dataset {0} '
                  'raised error: {1}'.format(dataset_name, error_type))
            print('#' * 10)


if __name__ == "__main__":
    DATESET_NAMES = [
        'FloatingCarData',
        'RedWineQuality',
        'RedWineQualityBinary',
        'SpeedAverages',
        'WhiteWineQuality',
        'WhiteWineQualityBinary'
    ]
    IS_USE_SMALL_SUBTABLE = False
    build_benchmark_graphs(dataset_names=DATESET_NAMES,
                            generate_small_ttl=IS_USE_SMALL_SUBTABLE)
