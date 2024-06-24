import { Board } from "./board";

export class Invitation {

    constructor(
        public id: number,
        public status: string,
        public expireAt: Date,
        public access: string,
        public userId: number,
        public board: Board
    ) { }
}
