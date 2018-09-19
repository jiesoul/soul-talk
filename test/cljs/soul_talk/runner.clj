(ns soul-talk.runner
  (:require [doo.runner :as doo]
            [soul-talk.core-test]))

(doo/doo-tests 'soul-talk.core-test)