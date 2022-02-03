from __future__ import annotations

from typing import Any, TypeVar, Optional, Callable


def eager_or(left_operand: bool, right_operand: bool) -> bool:
    return left_operand or right_operand


def eager_and(left_operand: bool, right_operand: bool) -> bool:
    return left_operand and right_operand


Elvis_T = TypeVar("Elvis_T")


def eager_elvis(left_operand: Elvis_T, right_operand: Elvis_T) -> Elvis_T:
    pass


Safe_Access_T = TypeVar("Safe_Access_T")


def safe_access(receiver: Any, lazy_member_access: Callable[[], Safe_Access_T]) -> Optional[Safe_Access_T]:
    pass
