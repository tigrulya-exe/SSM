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

import sys
import time
from util import *

size = sys.argv[1]
num = sys.argv[2]

#The data dir is named by case. Please see prepare.sh
case = size + "_" + num
log = sys.argv[3]
#Either "allssd" or "alldisk"
action = sys.argv[4]

if action == "allssd":
    rid = submit_rule("file: path matches \"/" + case + "/*\"| allssd")
elif action == "alldisk":
    rid = submit_rule("file: path matches \"/" + case + "/*\"| alldisk")

start_rule(rid)
start_time = time.time()
rule = get_rule(rid)
last_checked = rule['numChecked']
last_cmdsgen = rule['numCmdsGen']
time.sleep(.1)

#Check whether all expected cmdlets have been generated.
#The overall cmdlets' num should equal to the test files' num,
#if not, wait for more cmdlets to be generated.
cids = get_cids_of_rule(rid)
while len(cids) < int(num):
  time.sleep(.1)
  rule = get_rule(rid)
  cids = get_cids_of_rule(rid)
time.sleep(.1)
cids = get_cids_of_rule(rid)

wait_cmdlets(cids)

end_time = time.time()
stop_rule(rid)
# append result to log file
f = open(log, 'a')
f.write(str(int(end_time - start_time)) + "s" + "  " + '\n')
f.close()
