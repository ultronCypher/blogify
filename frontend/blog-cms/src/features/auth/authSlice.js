import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import api from "../../api/api";

const initialToken = localStorage.getItem("token") || null;

const initialState = {
    user: null,
    token: initialToken,
    status: initialToken ? "loading" : "idle",
    error: null,
    registerSuccess: false
};

export const fetchCurrentUser = createAsyncThunk(
    "auth/fetchCurrentUser",
    async (_, { rejectWithValue }) => {
        const token = localStorage.getItem("token");
        if (!token) {
            return rejectWithValue("No token found");
        }
        try {
            const response = await api.get("/auth/me");
            return { user: response.data, token };
        } catch (error) {
            localStorage.removeItem("token");
            const message =
                error?.response?.data?.message ||
                (typeof error?.response?.data === "string" ? error.response.data : null) ||
                "Session expired";
            return rejectWithValue(message);
        }
    }
);

export const loginUser = createAsyncThunk(
    "auth/loginUser",
    async (credentials, { rejectWithValue }) => {
        try {
            const response = await api.post("/auth/login", credentials);
            const token = response.data.token;
            if (token) {
                localStorage.setItem("token", token);
                const meRes = await api.get("/auth/me");
                return { token, user: meRes.data };
            } else {
                return rejectWithValue("Token not returned from server");
            }
        } catch (error) {
            const message =
                error?.response?.data?.message ||
                (typeof error?.response?.data === "string" ? error.response.data : null) ||
                "Login failed";
            return rejectWithValue(message);
        }
    }
);

export const registerUser = createAsyncThunk(
    "auth/registerUser",
    async (userData, { rejectWithValue }) => {
        try {
            const response = await api.post("/auth/register", userData);
            return response.data;
        } catch (error) {
            const message =
                error?.response?.data?.message ||
                (typeof error?.response?.data === "string" ? error.response.data : null) ||
                "User registration failed";
            return rejectWithValue(message);
        }
    }
);

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        logout(state) {
            localStorage.removeItem("token");
            state.user = null;
            state.token = null;
            state.status = "idle";
            state.error = null;
            state.registerSuccess = false;
        },
        updateUser(state, action) {
            if (state.user) {
                state.user = {
                    ...state.user,
                    ...action.payload
                };
            }
        },
        clearAuthError(state) {
            state.error = null;
        },
        resetRegisterSuccess(state) {
            state.registerSuccess = false;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(loginUser.pending, (state) => {
                state.status = "loading";
                state.error = null;
            })
            .addCase(loginUser.fulfilled, (state, action) => {
                state.status = "succeeded";
                state.token = action.payload.token;
                state.user = action.payload.user;
                state.error = null;
            })
            .addCase(loginUser.rejected, (state, action) => {
                state.status = "failed";
                state.error = action.payload;
            })
            .addCase(registerUser.pending, (state) => {
                state.status = "loading";
                state.error = null;
                state.registerSuccess = false;
            })
            .addCase(registerUser.fulfilled, (state) => {
                state.status = "succeeded";
                state.registerSuccess = true;
                state.error = null;
            })
            .addCase(registerUser.rejected, (state, action) => {
                state.status = "failed";
                state.error = action.payload;
                state.registerSuccess = false;
            })
            .addCase(fetchCurrentUser.pending, (state) => {
                state.status = "loading";
            })
            .addCase(fetchCurrentUser.fulfilled, (state, action) => {
                state.status = "succeeded";
                state.user = action.payload.user;
                state.token = action.payload.token;
                state.error = null;
            })
            .addCase(fetchCurrentUser.rejected, (state) => {
                state.status = "idle";
                state.user = null;
                state.token = null;
            });
    }
});

export const { logout, updateUser, clearAuthError, resetRegisterSuccess } = authSlice.actions;
export default authSlice.reducer;