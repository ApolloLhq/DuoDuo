# -*- coding: utf-8 -*-
# 系统命令相关的python功能.

import unittest

from utils import StringUtil


class TestStringUtilFunc(unittest.TestCase):
    def testIsEmpty(self):
        self.assertTrue(StringUtil.isEmpty(""))
        self.assertTrue(StringUtil.isEmpty(None))
        self.assertFalse(StringUtil.isEmpty("me"))

    def testSplit(self):
        strs = StringUtil.split(",,", ",")
        for i in range(len(strs)):
            self.assertEqual("", strs[i])

        strs = StringUtil.split("1,,", ",")
        self.assertEqual("1", strs[0])
        self.assertEqual("", strs[1])
        self.assertEqual("", strs[2])

        strs = StringUtil.split("1,,1", ",")
        self.assertEqual("1", strs[0])
        self.assertEqual("", strs[1])
        self.assertEqual("1", strs[2])

    def testArrayToString(self):
        list = [1, 2, 3]
        self.assertEqual("1,2,3", StringUtil.arrayToStr(list, ","))
        self.assertEqual("[1,2,3]", StringUtil.arrayToStr(list, ",", "[", "]"))
