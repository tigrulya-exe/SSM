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


class TestStressMover(unittest.TestCase):
  @classmethod
  def tearDownClass(cls):
    subprocess.call(f"hdfs dfs -rm -r {HDFS_TEST_DIR}", shell=True)

    def test_move_scheduler(self):
        file_paths = []
        cids = []
        failed_cids = []
        for i in range(MAX_NUMBER):
            file_paths.append(create_random_file(FILE_SIZE))
        for i in range(MAX_NUMBER):
            cids.append(move_randomly(file_paths[i]))
        while len(cids) != 0:
            cmd = wait_for_cmdlet(cids[0])
            if cmd['state'] == 'FAILED':
                failed_cids.append(cids[0])
            cids.pop(0)
        self.assertTrue(len(failed_cids) == 0)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-size', default='1MB',
                        help="size of file, Default Value 1MB.")
    parser.add_argument('-num', default='10000',
                        help="file num, Default Value 10000.")
    parser.add_argument('unittest_args', nargs='*')
    args, unknown_args = parser.parse_known_args()
    sys.argv[1:] = unknown_args
    print("The file size for test is {}.".format(args.size))
    FILE_SIZE = convert_to_byte(args.size)
    print("The file number for test is {}.".format(args.num))
    MAX_NUMBER = int(args.num)

    unittest.main()
