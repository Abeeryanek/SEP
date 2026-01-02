
import { UserRole } from "./UserRole";

export interface UserLoginResponse {
    username: string;
    role: UserRole;
    token: string
    profilePicturePath: string | null
}