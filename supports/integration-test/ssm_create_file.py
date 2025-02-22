#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import argparse
import unittest
from util import *


class CreateFile(unittest.TestCase):

    def test_create_file(self):
        cids = []
        for i in range(MAX_NUMBER):
            path, cid = create_random_file_parallel(FILE_SIZE, FILE_DIR)
            cids.append(cid)
        failed_cids = wait_for_cmdlets(cids)
        self.assertTrue(len(failed_cids) == 0, "Failed to create test files!")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-size', default='1MB')
    parser.add_argument('-num', default='10')
    default_dir = HDFS_TEST_DIR + random_string() + "/"
    parser.add_argument('-path', default=default_dir)
    parser.add_argument('unittest_args', nargs='*')
    args, unknown_args = parser.parse_known_args()
    sys.argv[1:] = unknown_args
    FILE_SIZE = convert_to_byte(args.size)
    print("The file size for test is {}.".format(FILE_SIZE))
    MAX_NUMBER = int(args.num)
    print("The file number for test is {}.".format(MAX_NUMBER))
    if not args.path.endswith("/"):
        args.path = args.path + "/"
    FILE_DIR = args.path
    print("The test path is {}.".format(FILE_DIR))

    unittest.main()