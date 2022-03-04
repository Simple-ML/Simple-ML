from typing import Optional, Union

from numpy.typing import ArrayLike
from simpleml.model.supervised._domain import DataType


def convert_to_array(
    o: Union[DataType, ArrayLike], type: Optional[str] = None
) -> ArrayLike:

    try:
        r = o.toArray()
    except AttributeError:
        r = o

    if type is not None:
        return r.astype(type)
    else:
        return r
