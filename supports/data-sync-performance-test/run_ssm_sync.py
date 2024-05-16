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

import numpy as np
import os
from util import *


size = sys.argv[1]
num = sys.argv[2]
case = size + "_" + num
log = sys.argv[3]
path = sys.argv[4]
dest_cluster = sys.argv[5]
ssm_base = sys.argv[6]

os.environ["SSM_BASE_URL"] = ssm_base

rule_string = f'file: path matches "{path}*" | sync -dest {dest_cluster}{path}'
rid = submit_rule(rule_string)
start_rule(rid)
start_time = time.time()
gen_cmds = int(num)


while True:
    time.sleep(2)
    rule = get_rule(rid)
    if rule['numCmdsGen'] >= gen_cmds:
        time.sleep(5)
        break


cids = get_cids_of_rule(rid)
# for async sync
while len(cids) != gen_cmds:
    gen_cmds = len(cids)
    time.sleep(5)
    cids = get_cids_of_rule(rid)


failed_cids = wait_for_cmdlets(cids)
if len(failed_cids) != 0:
    print("Not all sync actions succeeded!")
stop_rule(rid)
delete_rule(rid)
end_time = time.time()

# Append the timing result to log file
f = open(log, 'a')
f.write(f"{str(int(end_time - start_time))}s per sync with generate {gen_cmds} cmd. RuleId - {rid}  \n")

f.close()
