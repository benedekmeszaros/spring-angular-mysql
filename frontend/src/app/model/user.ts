export class User {

    constructor(
        public id: number,
        public email: string,
        public fullName: string,
        public createdAt: Date,
        public updatedAt: Date,
        public avatar: string
    ) { }
}
