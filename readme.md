Клиент-серверное android приложение.  
Механика следующая: пользователь на клиенте вводит mac адрес интересующего устройства. Клиент передает mac на сервер через любой глобальный канал (смс, почта, мессенджер, push, websocket и прочее). Важно чтобы передача осуществлялась не через локальную сеть.        
Сервер получает mac, определяет есть ли устройство с этим mac в его wi-fi сети в текущий момент и отвечает клиенту. Теперь пользователь видит состояние интересующего его устройства.  
  