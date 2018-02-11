# -*- coding: utf-8 -*-

# Comment

__author__ = 'qiunet'

from collections import Iterable


def isEmpty(o: object) -> bool:
    """
    判断空对象
    :param o:
    :return: True False
    """
    return o is None or len(o) == 0


def subObj(it: Iterable, start: int, end: int, step: int = 1) -> Iterable:
    """
    分割list
    :param it: 可迭代对象 
    :param start: 起始数
    :param end:  结束数
    :param step: 步长
    :return: 
    """
    if not isinstance(it, Iterable):
        raise Exception(str(it)+" Not Iterable Object")

    return it[start: end: step]