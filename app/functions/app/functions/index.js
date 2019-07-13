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

app.get('/posts/data', (req, res) => {

  try {
    let postsData = []
    db.collection('posts')
      .get()
      .then(snapshot => {
        snapshot.forEach((doc) => {
          postsData.push({
            id: doc.id,
            name: doc.data().name,
            lon: doc.data().longitude,
            lat: doc.data().latitude,
            desc: doc.data().description,
            userId: doc.data().userId
          })
        });
        return postsData
      })
      .then(posts => posts.map(post => ([post, db.collection('users').doc(post.userId).get()])))
      .then(tuples => tuples.map(tuple => {
        const obj =
        {
          id: tuple[0].id,
          name: tuple[0].name,
          lon: tuple[0].lon,
          lat: tuple[0].lat,
          desc: tuple[0].desc,
          userId: tuple[0].userId,
          username: tuple[1].username,
          user_name: tuple[1].name
        }
        return obj
      }))
      .then(datas => res.json(datas).status(200))
      .catch(err => console.log(err))
  } catch (error) {
    console.log(error)
  }


})





exports.app = functions.https.onRequest(app)

