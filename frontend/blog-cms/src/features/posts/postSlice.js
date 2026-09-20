import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import api from "../../api/api";

const initialState = {
    posts: [],
    selectedPost: null,
    status: "idle",
    error: null
}

const postSlice = createSlice({
    name: "posts",
    initialState,
    reducers: {

    }
})