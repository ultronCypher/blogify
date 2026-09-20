import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import api from "../../api/api";

const initialState = {
    posts: [],
    currentPage: 0,
    pageSize: 10,
    totalPages: 0,
    totalElements: 0,
    status: "idle",
    error: null
};

export const fetchPosts = createAsyncThunk(
    "posts/fetchPosts",
    async ({ page = 0, size = 10 } = {}) => {
        const response = await api.get(`/posts?page=${page}&size=${size}`);
        return response.data;
    }
);

const postSlice = createSlice({
    name: "posts",
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchPosts.pending, (state) => {
                state.status = "loading";
                state.error = null;
            })
            .addCase(fetchPosts.fulfilled, (state, action) => {
                state.status = "succeeded";
                state.posts = action.payload.content || [];
                state.currentPage = action.payload.number ?? action.payload.page ?? 0;
                state.pageSize = action.payload.size ?? 10;
                state.totalPages = action.payload.totalPages ?? 0;
                state.totalElements = action.payload.totalElements ?? 0;
            })
            .addCase(fetchPosts.rejected, (state, action) => {
                state.status = "failed";
                state.error = action.error.message || "Failed to fetch posts";
            });
    }
});

export default postSlice.reducer;