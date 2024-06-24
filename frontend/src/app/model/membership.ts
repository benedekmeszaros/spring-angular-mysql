import { User } from "./user";

export class Membership {

    constructor(
        public id: number,
        public access: string,
        public joinedAt: Date,
        public boardId: number,
        public user: User
    ) { }
}
