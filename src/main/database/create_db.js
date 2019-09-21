var db = db.getSiblingDB("restblogdb");

db.users.deleteMany( {} );
db.createCollection("users");

db.posts.deleteMany( {} );
db.createCollection("posts");

db.comments.deleteMany( {} );
db.createCollection("comments");

db.subscriptions.deleteMany( {} );
db.createCollection("subscriptions");

db.users.insertMany(
    [
        {
            name: "Usuario Prueba 1",
            nickname: "usuario_prueba1",
            password: "$2a$06$q.OHjDbaGnAip5mjBoQmZeffa2keSjlzwlsX9SQ0L5XLCNfhrfVLC",
            email: "prueba1@gmail.com",
            signupDate: new Date(),
            roles: ["ADMIN", "EDITOR", "MODERATOR", "READER"],
            suspended: false
        },
        {
            name: "Usuario Prueba 2",
            nickname: "usuario_prueba2",
            password: "$2a$06$rCpw5a.UL3ZR0Ewql.tX5OwG0RErf6.dlNlcfCbKYYqUTh0FlSVtm",
            email: "prueba2@gmail.com",
            signupDate: new Date(),
            roles: ["EDITOR", "MODERATOR", "READER"],
            suspended: false
        },
        {
            name: "Usuario Prueba 3",
            nickname: "usuario_prueba3",
            password: "$2a$06$VX/NvJyAnAIJNOA5Lnwgl.aL2.tmHHPZBlGCpT9o1GBRXGGSFXGjO",
            email: "prueba3@gmail.com",
            signupDate: new Date(),
            roles: ["MODERATOR", "READER"],
            suspended: false
        },
        {
            name: "Usuario Prueba 4",
            nickname: "usuario_prueba4",
            password: "$2a$06$h16h3AeINiV634eWkXM3oO/7cEr2dijnjAERqTNCa7.dLv2C3SAIu",
            email: "prueba4@gmail.com",
            signupDate: new Date(),
            roles: ["READER"],
            suspended: false
        }
    ]
);
