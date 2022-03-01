from simpleml.model.supervised._domain import DataType
from typing import Union, Optional
from numpy.typing import ArrayLike

def convert_to_array(o: Union[DataType, ArrayLike], type: Optional[str] = None) -> ArrayLike:
    
    try:
        r = o.toArray()
    except:
        r = o

    if type is not None:
        return r.astype(type)
    else:
        return r