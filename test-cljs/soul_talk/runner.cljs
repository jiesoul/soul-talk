(ns soul-talk.runner
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            [soul-talk.core-test]))

(doo-tests 'soul-talk.core-test)