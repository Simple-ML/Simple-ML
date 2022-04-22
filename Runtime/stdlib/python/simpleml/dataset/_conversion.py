from datetime import datetime

from simpleml.dataset._instance import Instance


class AttributeTransformer:
    def transform(self, instance: Instance):
        ...


class WeekendTransformer(AttributeTransformer):
    def __init__(self, attributeId: str):
        self._attribute_id = attributeId

    def transform(self, instance: Instance) -> bool:
        week_num = instance.getValue(self._attribute_id).weekday()
        if week_num < 5:
            return False
        else:
            return True


class DayOfTheYearTransformer(AttributeTransformer):
    def __init__(self, attributeId: str):
        self._attribute_id = attributeId

    def transform(self, instance: Instance) -> int:
        return instance.getValue(self._attribute_id).timetuple().tm_yday


class WeekDayTransformer(AttributeTransformer):
    def __init__(self, attributeId: str):
        self._attribute_id = attributeId

    def transform(self, instance: Instance) -> str:
        return instance.getValue(self._attribute_id).strftime("%A")


class TimestampTransformer(AttributeTransformer):
    def __init__(self, attributeId: str):
        self._attribute_id = attributeId

    def transform(self, instance: Instance) -> float:
        return datetime.timestamp(instance.getValue(self._attribute_id))
