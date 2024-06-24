import { User } from "./user";

export class Task {

    constructor(
        public id: number,
        public pos: number,
        public editable: boolean,
        public arrangeable: boolean,
        public label: string,
        public description: string,
        public createdAt: Date,
        public createdBy: User,
        public editedAt: Date,
        public editedBy: User,
        public thumbnail: string
    ) { }
}
