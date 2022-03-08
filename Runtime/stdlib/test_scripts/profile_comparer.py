from simpleml.dataset import loadDataset
from simpleml.util import exportDictionaryAsJSON


def compareStats(dictionary1, dictionary2, info):

    if type(dictionary1) != type(dictionary2):
        print("different types:", info, type(dictionary1), type(dictionary2))
        return

    if type(dictionary1) == list:
        if len(dictionary1) != len(dictionary2):
            print(info, "Different list lengths")
            return
        for i in range(0, len(dictionary1)):
            return compareStats(
                dictionary1[i], dictionary2[i], info + "-> [" + str(i) + "]"
            )

    if type(dictionary1) != dict or type(dictionary2) != dict:
        if not isclose(dictionary1, dictionary2):
            print("Different values:", info, dictionary1, dictionary2)
        return
    for key in dictionary1.keys():
        if key not in dictionary2:
            print("Missing key in dictionary2: ", info + "->" + key)
        else:
            compareStats(dictionary1[key], dictionary2[key], info + "->" + key)


def isclose(a, b, rel_tol=1e-09, abs_tol=0.0):
    if type(a) == float or type(a) == int:
        return abs(a - b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)
    else:
        return a == b


dataset = loadDataset("WhiteWineQuality")
print("From Catalog:")
print(exportDictionaryAsJSON(dataset.getProfile()))

dataset_sampled = dataset.sample(100000)

# compute statistics from the dataset
print("From stats:")
print(exportDictionaryAsJSON(dataset_sampled.getProfile()))

compareStats(dataset.getProfile(), dataset_sampled.getProfile(), "")
