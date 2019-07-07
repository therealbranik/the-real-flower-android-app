const functions = require('firebase-functions');
const express = require('express')
const app = express()
const port = 3000
const admin = require('firebase-admin')
admin.initializeApp()
let db = admin.firestore()

app.listen(port, () => {
    console.log('Listening port ' + port)
})

app.get('/delete-old-positions/:minutes', (req, res) => {
    try {
    const minutes = req.params.minutes
    let dateOffset = (1000 * 60 * minutes)
    let myDate = new Date()
    myDate.setTime(myDate.getTime() - dateOffset)
    
    let positions = []
    db.collection('positions')
        .where("timestamp", "<", myDate)
        .get()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                positions.push(doc.data())
              doc.ref.delete() 
                // console.log(doc.id, '=>', doc.data());
            });
            res.json(positions).status(200)
            return true
          })
          .catch((err) => {
            console.log('Error getting documents', err)
          });    
    } catch (error) {
        console.log(error)
    }
    
})

exports.app = functions.https.onRequest(app)