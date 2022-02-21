(ns portal-nrepl-1.pipeline
  (:require [clojure.core.async :as async
             :refer [<! go go-loop timeout chan thread]]
            [portal.api :as portal]))

(defn handle-value [{:keys [code value] :as event}]
  (let [value-to-submit
        (if (and (vector? value)
                 (-> value first keyword?))
          (-> value
              (with-meta {:portal.viewer/default
                          :portal.viewer/hiccup}))
          value)]
    (portal/submit value-to-submit)))

(portal/submit 9)

(defn handle [{:keys [event-type]
               :as event}]
  (case event-type
    :event-type/value (handle-value event)))

(defn new-pipeline [handler]
  (portal/open)
  (let [events-channel         (async/chan 100)]
    (async/go-loop []
      (handler (async/<! events-channel))
      (recur))
    {:stop (fn []
             (async/close! events-channel))
     :process (fn [event]
                (async/>!! events-channel event))}))

(defonce *pipeline
  (atom nil))

(defn restart []
  (portal/close)
  (when-let [s (:stop @*pipeline)]
    (s))
  (reset! *pipeline (new-pipeline #'handle)))

(defn start []
  (restart))

(defn process [event]
  (when-let [p (:process @*pipeline)]
    (p event)))
