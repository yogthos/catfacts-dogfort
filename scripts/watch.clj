(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'catfacts.core
   :output-to "out/catfacts.js"
   :output-dir "out"})
