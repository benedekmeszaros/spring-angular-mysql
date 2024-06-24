import { Membership } from "./membership";
import { Phase } from "./phase";
import { User } from "./user";

export class Board {

    constructor(
        public id: number,
        public title: string,
        public description: string,
        public createdAt: Date,
        public updatedAt: Date,
        public updatedBy: User,
        public owner: User,
        public phases: Phase[],
        public members: Membership[],
    ) { }

}
