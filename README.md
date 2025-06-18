## Trading API Project
- Java version: 17
- Maven version: 3.8.8

-----
## Request Example
- GET: 
  + Get latest best pricing: http://localhost:8080/trading/best
  + Get current wallet balance: http://localhost:8080/trading/wallet
  + Get trading history: http://localhost:8080/trading/history
- POST
  + Trade:  http://localhost:8080/trading/trade
  +  sample body : <br> { <Br>
    "type": "BTCUSDT",<br>
    "buyQuantity": 0.02, <br>
    "tradingMethod": "BUY" <br>
    }
    +  sample body: <Br> { <Br>
    "type": "BTCUSDT", or <br>
      "sellQuantity": 0.01, <br>
      "tradingMethod": "SELL"  <br>
      }
