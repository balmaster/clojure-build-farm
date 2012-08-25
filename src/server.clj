(ns server)

(defn audit
  "Провести аудит (сравнить с дистрибутивом)"
  [env distribute]
  ())
  
(defn audit
  "Провести аудит (что изменится если я применю изменения)"
  [env distribute changes]
  ())

(defn migrate
  "Запланировать действия по миграции с дистрибутива x на дистрибутив y"
  [env x y]
  ())

(defn migrate-component
  "Запланировать действия по миграции компонента с дистрибутива x на дистрибутив y"
  [env x y component changes]
  ())

(defn apply
  "Применить запланированные изменения"
  [env distribute changes]
  ())

