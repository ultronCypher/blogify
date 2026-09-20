export const selectCurrentUser = (state) => state.auth.user;
export const selectAuthToken = (state) => state.auth.token;
export const selectAuthStatus = (state) => state.auth.status;
export const selectAuthError = (state) => state.auth.error;
export const selectIsAuthenticated = (state) => Boolean(state.auth.user && state.auth.token);
export const selectAuthLoading = (state) => state.auth.status === "loading";
export const selectRegisterSuccess = (state) => state.auth.registerSuccess;

